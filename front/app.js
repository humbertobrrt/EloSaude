
/* Unidades vêm da API (GET /api/unidades-saude). Começa vazio e é preenchido por carregarUnidades(). */
let unidades = [];
let erroUnidades = "";

/* Converte o formato da API para o usado nas telas (esp = array de especialidades) */
function normalizarUnidade(u) {
    return {
        id: u.id,
        nome: u.nome || "",
        tipo: u.tipo || "",
        bairro: u.bairro || "",
        cidade: u.cidade || "",
        telefone: u.telefone || "",
        esp: typeof u.especialidades === "string"
            ? u.especialidades.split(",").map(e => e.trim()).filter(Boolean)
            : Array.isArray(u.especialidades) ? u.especialidades : [],
        ativa: u.ativa !== false
    };
}

async function carregarUnidades() {
    try {
        const dados = await Api.listarUnidades();
        const lista = Array.isArray(dados) ? dados : (dados?.content || []);
        unidades = lista.map(normalizarUnidade).filter(u => u.ativa);
        erroUnidades = "";
        console.log("Unidades carregadas da API:", unidades);
    } catch (erro) {
        erroUnidades = `${erro.message}${erro.status ? " (HTTP " + erro.status + ")" : ""}`;
        console.error("Falha ao carregar unidades:", erro);
        toast(erro.message || "Não foi possível carregar as unidades.");
    }
}

const inicial = {
    fichas: [
        { id: "F-1024", paciente: "Maria Souza", unidadeId: 1, motivo: "Controle de pressão arterial", status: "Agendada", data: "2026-10-20" },
        { id: "F-1025", paciente: "João Lima", unidadeId: 2, motivo: "Dor lombar persistente", status: "Em triagem", data: "2026-10-22" }
    ],
    registros: [
        { id: "R-1", paciente: "Maria Souza", profissional: "Dra. Ana Ribeiro", unidadeId: 1, data: "2026-10-20", conduta: "Ajuste de losartana 50mg", obs: "PA 150x95. Encaminhada à cardiologia." }
    ],
    enc: [
        { id: "E-301", paciente: "Maria Souza", origemId: 1, destinoId: 3, esp: "Cardiologia", resumo: "Hipertensão de difícil controle.", comp: ["Medicações em uso", "Últimos exames", "Alergias"], status: "Recebido", data: "2026-10-20" }
    ],
    receitas: [
        { id: "RX-1", paciente: "Maria Souza", medico: "Dra. Ana Ribeiro", medicamento: "Losartana 50 mg", posologia: "1 comprimido pela manhã, uso contínuo", validade: "2026-11-20", data: "2026-10-20" }
    ],
    exames: [
        { id: "EX-1", paciente: "Maria Souza", exame: "Hemograma completo", urgencia: "Rotina", status: "Concluída", resultado: "Dentro dos valores de referência.", data: "2026-10-20" },
        { id: "EX-2", paciente: "Maria Souza", exame: "Eletrocardiograma", urgencia: "Rotina", status: "Solicitada", data: "2026-10-20" }
    ],
    consent: {}
};

const $ = seletor => document.querySelector(seletor);

function carregarDados() {
    try {
        const dados = JSON.parse(localStorage.getItem("elosaude"));
        if (dados && Array.isArray(dados.fichas) &&
            Array.isArray(dados.registros) && Array.isArray(dados.enc)) {
            return dados;
        }
    } catch (erro) {
        console.error("Erro ao carregar dados:", erro);
    }
    return JSON.parse(JSON.stringify(inicial));
}

let S = carregarDados();
for (const chave of ["receitas", "exames"]) S[chave] ??= JSON.parse(JSON.stringify(inicial[chave]));
S.consent ??= {};

const INFO = ["Medicações em uso", "Últimos exames", "Alergias", "Histórico de consultas"];
const aberta = f => !["Concluída", "Cancelada"].includes(f.status);
const statusReceita = r => r.validade < hoje() ? "Vencida" : "Ativa";
const emDias = n => { const d = new Date(); d.setDate(d.getDate() + n); return d.toLocaleDateString("sv-SE"); };
let usuario = lerSessao();

const esc = texto => String(texto ?? "").replace(/[&<>"']/g, c => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
})[c]);

const nome = id => unidades.find(u => String(u.id) === String(id))?.nome || "—";
const novoId = prefixo => prefixo + "-" + Date.now().toString(36);

const hoje = () => {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
};

const tag = status => `<span class="tag s-${esc(status.split(" ")[0])}">${esc(status)}</span>`;

const opts = sel => unidades.map(u =>
    `<option value="${esc(u.id)}"${String(u.id) === String(sel) ? " selected" : ""}>${esc(u.nome)}</option>`
).join("");

const form = evento => Object.fromEntries(new FormData(evento.target));

function toast(mensagem) {
    const elemento = document.createElement("div");
    elemento.className = "toast";
    elemento.textContent = mensagem;
    document.body.append(elemento);
    setTimeout(() => elemento.remove(), 2500);
}

function save() {
    try {
        localStorage.setItem("elosaude", JSON.stringify(S));
        render();
    } catch (erro) {
        console.error(erro);
        toast("Não foi possível salvar os dados.");
    }
}

/* LOGIN */

function telaLogin() {
    $("#nav").style.display = "none";

    $("#app").innerHTML = `
    <section class="login-page">
      <div class="login-card">
        <div class="login-brand">
          <div class="login-symbol">+</div>
          <h1>Elo<span style="color:var(--pri)">Saúde</span></h1>
          <p>A informação conecta.<br>O cuidado continua.</p>
        </div>

        <form id="loginForm">
          <label for="email">E-mail institucional</label>
          <input id="email" type="email" placeholder="seu@email.com"
            autocomplete="username" required>

          <label for="senha">Senha</label>
          <input id="senha" type="password" placeholder="Digite sua senha"
            autocomplete="current-password" required>

          <button class="login-button" type="submit">Entrar no sistema →</button>
          <p id="loginError" class="login-error" role="alert"></p>
        </form>
      </div>
    </section>
  `;

    $("#loginForm").addEventListener("submit", async evento => {
        evento.preventDefault();
        const botao = evento.target.querySelector("button");
        botao.disabled = true;

        try {
            usuario = await entrar($("#email").value, $("#senha").value);
            location.hash = "#/";
            render();
            toast("Bem-vindo(a), " + usuario.nome + "!");
        } catch (e) {
            $("#loginError").textContent = e.status === 401 ? "E-mail ou senha incorretos." : e.message;
            botao.disabled = false;
        }
    });
}

function sair() {
    usuario = null;
    Sessao.limpar();
    location.href = "login.html";
    render();
    toast("Você saiu do sistema.");
}

/* UNIDADES */

function listaUni(termo = "") {
    const busca = termo.toLocaleLowerCase("pt-BR");

    const encontradas = unidades.filter(u =>
        [u.nome, u.tipo, u.bairro, ...u.esp]
            .join(" ")
            .toLocaleLowerCase("pt-BR")
            .includes(busca)
    );

    if (!encontradas.length) {
        return erroUnidades
            ? `<p class='muted'>Não foi possível carregar as unidades: ${esc(erroUnidades)}</p>`
            : "<p class='muted'>Nenhuma unidade encontrada.</p>";
    }

    return encontradas.map(u => `
    <div class="card">
      <span class="tag">${esc(u.tipo)}</span>
      <h3>${esc(u.nome)}</h3>
      <p class="muted">${esc(u.bairro)}</p>
      ${u.esp.map(e => `<span class="tag">${esc(e)}</span>`).join("")}
    </div>
  `).join("");
}

/* MENUS POR PERFIL */

const NAV = {
    medico: [["/", "Painel"], ["/pacientes", "Pacientes"], ["/fichas", "Fichas"], ["/prontuario", "Prontuário"], ["/receitas", "Receitas"], ["/exames", "Exames"], ["/encaminhamentos", "Encaminhamentos"], ["/unidades", "Unidades"]],
    paciente: [["/", "Início"], ["/fichas", "Minhas fichas"], ["/prontuario", "Meu histórico"], ["/receitas", "Receitas"], ["/exames", "Exames"], ["/encaminhamentos", "Encaminhamentos"], ["/privacidade", "Privacidade"], ["/unidades", "Unidades"]]
};

/* TELAS DO MÉDICO */

const pagesMedico = {
    "/": () => {
        const fila = [
            ...S.exames.filter(x => x.status === "Solicitada").map(x => ({ h: "#/exames", s: x.status, t: `Exame ${x.id} · ${x.paciente}: ${x.exame}` })),
            ...S.enc.filter(e => e.status === "Pendente" || e.status === "Recebido").map(e => ({
                h: "#/encaminhamentos", s: e.status, t: `Encaminhamento ${e.id} · ${e.paciente}: ${e.esp}`
            })),
            ...S.fichas.filter(aberta).map(f => ({
                h: "#/fichas", s: f.status, t: `Ficha ${f.id} · ${f.paciente}: ${f.motivo}`
            }))
        ];

        return `
    <h2>Olá, ${esc(usuario.nome)}</h2>
    ${usuario.unidadeId ? `<p class="muted">${esc(nome(usuario.unidadeId))}</p>` : ""}

    <div class="grid stagger">
      <div class="card stat"><b>${S.fichas.filter(aberta).length}</b>fichas em aberto</div>
      <div class="card stat"><b>${S.enc.filter(e => e.status === "Pendente").length}</b>encaminhamentos a receber</div>
      <div class="card stat"><b>${S.enc.filter(e => e.status === "Recebido").length}</b>aguardando retorno</div>
      <div class="card stat"><b>${S.registros.length}</b>registros no prontuário</div>
    </div>

    <h2 style="margin-top:40px">Fila de atendimento</h2>
    <div class="list stagger">
      ${fila.map(p => `
        <a class="card row" href="${p.h}" style="text-decoration:none;color:inherit">
          <span>${esc(p.t)}</span>${tag(p.s)}
        </a>`).join("") || "<p class='muted'>Nenhuma pendência no momento.</p>"}
    </div>
    <p style="margin-top:20px"><a href="#/encaminhamentos"><button>Criar encaminhamento</button></a></p>
  `;
    },

    "/unidades": () => `
    <h2>Unidades de saúde</h2>
    <p class="muted">Rede cadastrada no EloSaúde.</p>
    <input id="busca" placeholder="Buscar por nome, bairro ou especialidade..." style="width:100%;margin-top:12px">
    <div class="grid stagger" id="lu">${listaUni()}</div>
  `,

    "/fichas": () => `
    <div class="two">
      <div class="card">
        <h2>Solicitar ficha</h2>
        <p class="muted">Peça um atendimento e acompanhe o status.</p>
        <form id="fFicha">
          <label>Paciente<input name="paciente" required></label>
          <label>Unidade<select name="unidadeId">${opts(usuario.unidadeId)}</select></label>
          <label>Motivo<textarea name="motivo" required></textarea></label>
          <button type="submit">Solicitar ficha</button>
        </form>
      </div>

      <div class="list stagger">
        ${S.fichas.map(f => `
          <div class="card">
            <div class="row"><b>${esc(f.id)} · ${esc(f.paciente)}</b>${tag(f.status)}</div>
            <p>${esc(f.motivo)}</p>
            <div class="row">
              <small class="muted">${esc(nome(f.unidadeId))} · ${esc(f.data)}</small>
              ${aberta(f) ? `<button class="ghost" data-av="${esc(f.id)}">Avançar status</button>` : ""}
            </div>
          </div>
        `).join("")}
      </div>
    </div>
  `,

    "/prontuario": () => `
    <div class="two">
      <div class="card">
        <h2>Registrar consulta</h2>
        <form id="fReg">
          <label>Paciente<input name="paciente" required></label>
          <label>Profissional<input name="profissional" value="${esc(usuario.nome)}" readonly></label>
          <label>Unidade<select name="unidadeId">${opts(usuario.unidadeId)}</select></label>
          <label>Conduta<input name="conduta" required></label>
          <label>Observações<textarea name="obs"></textarea></label>
          <button type="submit">Salvar no prontuário</button>
        </form>
      </div>

      <div class="list stagger">
        ${S.registros.map(r => `
          <div class="card">
            <div class="row"><b>${esc(r.paciente)}</b><small class="muted">${esc(r.data)}</small></div>
            <p><b>Conduta:</b> ${esc(r.conduta)}</p>
            <p class="muted">${esc(r.obs)}</p>
            <small>${esc(r.profissional)} · ${esc(nome(r.unidadeId))}</small>
          </div>
        `).join("")}
      </div>
    </div>
  `,

    "/encaminhamentos": () => `
    <div class="two">
      <div class="card">
        <h2>Novo encaminhamento</h2>
        <form id="fEnc">
          <label>Paciente<input name="paciente" required></label>
          <label>Origem<select name="origemId">${opts(usuario.unidadeId)}</select></label>
          <label>Destino<select name="destinoId">${opts(unidades.find(u => u.tipo !== "UBS")?.id)}</select></label>
          <label>Especialidade<input name="esp" required></label>
          <label>Resumo clínico<textarea name="resumo" required></textarea></label>

          <label>Informações autorizadas <small class="muted">(o paciente pode restringir)</small></label>
          <div>
            ${["Medicações em uso", "Últimos exames", "Alergias", "Histórico de consultas"].map(c => `
              <label style="display:flex;gap:8px;font-weight:500">
                <input type="checkbox" name="comp" value="${c}" checked>${c}
              </label>
            `).join("")}
          </div>

          <button type="submit">Encaminhar</button>
        </form>
      </div>

      <div class="list stagger">
        ${S.enc.map(e => `
          <div class="card">
            <div class="row"><b>${esc(e.id)} · ${esc(e.paciente)}</b>${tag(e.status)}</div>
            <div class="row" style="margin-top:8px">
              <small>${esc(nome(e.origemId))}</small>
              <small>→ ${esc(nome(e.destinoId))}</small>
            </div>
            <div class="flow"></div>
            <p><b>${esc(e.esp)}:</b> ${esc(e.resumo)}</p>
            <div>${(e.comp || []).map(c => `<span class="tag">${esc(c)}</span>`).join("")}</div>

            ${e.retorno ? `<p><b>Retorno:</b> ${esc(e.retorno)}</p>` : ""}
            ${e.status === "Pendente" ? `<button data-rec="${esc(e.id)}">Confirmar recebimento</button>` : ""}

            ${e.status === "Recebido" ? `
              <form data-ret="${esc(e.id)}" style="margin-top:10px">
                <input name="r" placeholder="Registrar retorno para a UBS..." required>
                <button type="submit">Registrar atendimento</button>
              </form>
            ` : ""}
          </div>
        `).join("")}
      </div>
    </div>
  `
};

/* TELAS DO PACIENTE (mostram só os dados dele) */

const meus = lista => lista.filter(x => x.paciente === usuario.nome);
const vazio = texto => `<p class="muted">${texto}</p>`;

const pagesPaciente = {
    "/": () => {
        const f = meus(S.fichas), e = meus(S.enc), r = meus(S.registros);
        const andamento = [
            ...f.filter(aberta).map(x => `
              <div class="card row"><span><b>Ficha ${esc(x.id)}</b><br>
              <small class="muted">${esc(x.motivo)} · ${esc(nome(x.unidadeId))}</small></span>${tag(x.status)}</div>`),
            ...e.filter(x => x.status !== "Atendido").map(x => `
              <div class="card row"><span><b>Encaminhamento para ${esc(x.esp)}</b><br>
              <small class="muted">${esc(nome(x.origemId))} → ${esc(nome(x.destinoId))}</small></span>${tag(x.status)}</div>`)
        ];

        return `
    <h2>Olá, ${esc(usuario.nome.split(" ")[0])}</h2>
    <p class="muted">Acompanhe seus atendimentos na rede de saúde.</p>

    <div class="grid stagger">
      <div class="card stat"><b>${f.length}</b>fichas solicitadas</div>
      <div class="card stat"><b>${r.length}</b>consultas no histórico</div>
      <div class="card stat"><b>${e.length}</b>encaminhamentos</div>
      <div class="card stat"><b>${meus(S.receitas).length}</b>receitas</div>
    </div>

    <h2 style="margin-top:40px">Em andamento</h2>
    <div class="list stagger">${andamento.join("") || vazio("Nada em andamento. Solicite uma ficha para ser atendido.")}</div>
    <p style="margin-top:20px"><a href="#/fichas"><button>Solicitar ficha</button></a></p>
  `;
    },

    "/unidades": () => pagesMedico["/unidades"](),

    "/fichas": () => `
    <div class="two">
      <div class="card">
        <h2>Solicitar ficha</h2>
        <p class="muted">Peça um atendimento e acompanhe o status.</p>
        <form id="fFicha">
          <label>Paciente<input name="paciente" value="${esc(usuario.nome)}" readonly></label>
          <label>Unidade<select name="unidadeId">${opts(usuario.unidadeId)}</select></label>
          <label>Motivo<textarea name="motivo" required></textarea></label>
          <button type="submit">Solicitar ficha</button>
        </form>
      </div>

      <div class="list stagger">
        ${meus(S.fichas).map(f => `
          <div class="card">
            <div class="row"><b>${esc(f.id)}</b>${tag(f.status)}</div>
            <p>${esc(f.motivo)}</p>
            <div class="row"><small class="muted">${esc(nome(f.unidadeId))} · ${esc(f.data)}</small>
            ${["Solicitada", "Em triagem"].includes(f.status) ? `<button class="ghost" data-canc="${esc(f.id)}">Cancelar ficha</button>` : ""}</div>
          </div>
        `).join("") || vazio("Você ainda não solicitou nenhuma ficha.")}
      </div>
    </div>
  `,

    "/prontuario": () => `
    <h2>Meu histórico</h2>
    <p class="muted">Consultas registradas pelos profissionais que atenderam você.</p>
    <div class="list stagger" style="margin-top:20px">
      ${meus(S.registros).map(r => `
        <div class="card">
          <div class="row"><b>${esc(r.conduta)}</b><small class="muted">${esc(r.data)}</small></div>
          <p class="muted">${esc(r.obs)}</p>
          <small>${esc(r.profissional)} · ${esc(nome(r.unidadeId))}</small>
        </div>
      `).join("") || vazio("Nenhuma consulta registrada ainda.")}
    </div>
  `,

    "/encaminhamentos": () => `
    <h2>Meus encaminhamentos</h2>
    <p class="muted">Veja para onde você foi encaminhado e quais informações foram compartilhadas.</p>
    <div class="list stagger" style="margin-top:20px">
      ${meus(S.enc).map(e => `
        <div class="card">
          <div class="row"><b>${esc(e.esp)}</b>${tag(e.status)}</div>
          <div class="row" style="margin-top:8px">
            <small>${esc(nome(e.origemId))}</small><small>→ ${esc(nome(e.destinoId))}</small>
          </div>
          <p>${esc(e.resumo)}</p>
          <div>${(e.comp || []).map(c => `<span class="tag">${esc(c)}</span>`).join("")}</div>
          ${e.retorno ? `<p><b>Retorno:</b> ${esc(e.retorno)}</p>` : ""}
        </div>
      `).join("") || vazio("Você não tem encaminhamentos.")}
    </div>
  `
};

/* FUNÇÕES DO MÉDICO: pacientes, receitas e exames */

const nomesPacientes = () => [...new Set([...S.fichas, ...S.registros, ...S.enc, ...S.receitas, ...S.exames].map(x => x.paciente))]
    .sort((a, b) => a.localeCompare(b, "pt-BR"));

const datalist = () => `<datalist id="pacs">${nomesPacientes().map(n => `<option value="${esc(n)}">`).join("")}</datalist>`;

function listaPac(termo = "") {
    const busca = termo.toLocaleLowerCase("pt-BR");
    return nomesPacientes().filter(n => n.toLocaleLowerCase("pt-BR").includes(busca)).map(n => {
        const por = lista => lista.filter(x => x.paciente === n);
        const reg = por(S.registros), rec = por(S.receitas), exa = por(S.exames);
        return `
      <div class="card">
        <div class="row"><b>${esc(n)}</b>
          <small class="muted">${por(S.fichas).length} fichas · ${reg.length} consultas · ${rec.length} receitas · ${exa.length} exames · ${por(S.enc).length} encaminhamentos</small></div>
        <details><summary>Ver histórico</summary>
          ${reg.map(r => `<p><b>${esc(r.data)}</b> · ${esc(r.conduta)}<br><small class="muted">${esc(r.obs)}</small></p>`).join("")}
          ${rec.map(r => `<p>Receita: ${esc(r.medicamento)}, ${esc(r.posologia)}</p>`).join("")}
          ${exa.map(x => `<p>Exame: ${esc(x.exame)} ${tag(x.status)}${x.resultado ? `<br><small class="muted">${esc(x.resultado)}</small>` : ""}</p>`).join("")}
          ${reg.length + rec.length + exa.length ? "" : vazio("Sem registros clínicos ainda.")}
        </details>
      </div>`;
    }).join("") || vazio("Nenhum paciente encontrado.");
}

Object.assign(pagesMedico, {
    "/pacientes": () => `
    <h2>Pacientes</h2>
    <p class="muted">Consulte o histórico completo de cada paciente.</p>
    <input id="buscaPac" placeholder="Buscar paciente pelo nome..." style="width:100%;margin-top:12px">
    <div class="list stagger" id="lp" style="margin-top:20px">${listaPac()}</div>
  `,

    "/receitas": () => `
    <div class="two">
      <div class="card">
        <h2>Emitir receita</h2>
        <form id="fRec">
          <label>Paciente<input name="paciente" list="pacs" required></label>${datalist()}
          <label>Medicamento<input name="medicamento" required></label>
          <label>Posologia<input name="posologia" placeholder="Ex.: 1 comprimido a cada 12 horas" required></label>
          <label>Validade<input name="validade" type="date" value="${emDias(30)}" required></label>
          <button type="submit">Emitir receita</button>
        </form>
      </div>
      <div class="list stagger">
        ${S.receitas.map(r => `
          <div class="card">
            <div class="row"><b>${esc(r.medicamento)}</b>${tag(statusReceita(r))}</div>
            <p>${esc(r.posologia)}</p>
            <small class="muted">${esc(r.paciente)} · ${esc(r.medico)} · válida até ${esc(r.validade)}</small>
          </div>`).join("") || vazio("Nenhuma receita emitida.")}
      </div>
    </div>
  `,

    "/exames": () => `
    <div class="two">
      <div class="card">
        <h2>Solicitar exame</h2>
        <form id="fExa">
          <label>Paciente<input name="paciente" list="pacs" required></label>${datalist()}
          <label>Exame<input name="exame" required></label>
          <label>Urgência<select name="urgencia"><option>Rotina</option><option>Urgente</option></select></label>
          <button type="submit">Solicitar exame</button>
        </form>
      </div>
      <div class="list stagger">
        ${S.exames.map(x => `
          <div class="card">
            <div class="row"><b>${esc(x.exame)}</b>${tag(x.status)}</div>
            <small class="muted">${esc(x.paciente)} · ${esc(x.urgencia)} · ${esc(x.data)}</small>
            ${x.resultado ? `<p><b>Resultado:</b> ${esc(x.resultado)}</p>` : ""}
            ${x.status === "Solicitada" ? `
              <form data-res="${esc(x.id)}" style="margin-top:10px">
                <input name="r" placeholder="Resultado do exame..." required>
                <button type="submit">Lançar resultado</button>
              </form>` : ""}
          </div>`).join("") || vazio("Nenhum exame solicitado.")}
      </div>
    </div>
  `
});

/* FUNÇÕES DO PACIENTE: receitas, exames e privacidade */

Object.assign(pagesPaciente, {
    "/receitas": () => `
    <h2>Minhas receitas</h2>
    <p class="muted">Receitas emitidas pelos médicos que atenderam você.</p>
    <div class="list stagger" style="margin-top:20px">
      ${meus(S.receitas).map(r => `
        <div class="card">
          <div class="row"><b>${esc(r.medicamento)}</b>${tag(statusReceita(r))}</div>
          <p>${esc(r.posologia)}</p>
          <small class="muted">${esc(r.medico)} · válida até ${esc(r.validade)}</small>
        </div>`).join("") || vazio("Você não tem receitas.")}
    </div>
  `,

    "/exames": () => `
    <h2>Meus exames</h2>
    <p class="muted">Acompanhe os exames pedidos e veja os resultados.</p>
    <div class="list stagger" style="margin-top:20px">
      ${meus(S.exames).map(x => `
        <div class="card">
          <div class="row"><b>${esc(x.exame)}</b>${tag(x.status)}</div>
          <p>${x.resultado ? `<b>Resultado:</b> ${esc(x.resultado)}` : "<span class='muted'>Aguardando resultado.</span>"}</p>
          <small class="muted">Pedido em ${esc(x.data)} · ${esc(x.urgencia)}</small>
        </div>`).join("") || vazio("Você não tem exames pedidos.")}
    </div>
  `,

    "/privacidade": () => {
        const aut = S.consent[usuario.nome] || INFO;
        return `
    <h2>Privacidade</h2>
    <p class="muted">Escolha quais informações podem ser compartilhadas quando você for encaminhado a outra unidade.</p>
    <div class="card" style="margin-top:20px;max-width:520px">
      <form id="fPriv">
        ${INFO.map(c => `
          <label style="display:flex;gap:8px;font-weight:500">
            <input type="checkbox" name="aut" value="${c}" ${aut.includes(c) ? "checked" : ""}>${c}
          </label>`).join("")}
        <button type="submit">Salvar autorizações</button>
      </form>
    </div>
  `;
    }
});

/* RENDERIZAÇÃO */

function render() {
    if (!usuario) {
        telaLogin();
        return;
    }

    const medico = usuario.perfil === "medico";
    const menu = NAV[usuario.perfil];
    const pedida = location.hash.slice(1) || "/";
    const rota = menu.some(([h]) => h === pedida) ? pedida : "/";
    const conteudo = (medico ? pagesMedico : pagesPaciente)[rota]();

    $("#nav").style.display = "";
    $("#nav").innerHTML = menu.map(([h, t]) => `<a href="#${h}">${t}</a>`).join("");

    $("#app").innerHTML = `
    <div class="page">
      <div class="row" style="margin-bottom:16px">
        <span class="muted">${esc(usuario.nome)} <span class="tag">${medico ? "Médico" : "Paciente"}</span></span>
        <button type="button" class="ghost" id="logout">Sair</button>
      </div>
      ${conteudo}
    </div>
  `;

    document.querySelectorAll("#nav a").forEach(a => {
        a.classList.toggle("on", a.getAttribute("href") === "#" + rota);
    });

    $("#logout").addEventListener("click", sair);
    bind();
}

/* EVENTOS */

const exige = perfil => {
    if (usuario?.perfil === perfil) return true;
    toast("Essa ação não está disponível para o seu perfil.");
    return false;
};

function bind() {
    $("#buscaPac")?.addEventListener("input", e => { $("#lp").innerHTML = listaPac(e.target.value); });

    $("#fRec")?.addEventListener("submit", e => {
        e.preventDefault();
        if (!exige("medico")) return;
        S.receitas.unshift({ ...form(e), id: novoId("RX"), medico: usuario.nome, data: hoje() });
        save();
        toast("Receita emitida!");
    });

    $("#fExa")?.addEventListener("submit", e => {
        e.preventDefault();
        if (!exige("medico")) return;
        S.exames.unshift({ ...form(e), id: novoId("EX"), status: "Solicitada", data: hoje() });
        save();
        toast("Exame solicitado!");
    });

    document.querySelectorAll("[data-res]").forEach(f => f.addEventListener("submit", e => {
        e.preventDefault();
        if (!exige("medico")) return;
        const exame = S.exames.find(x => x.id === f.dataset.res);
        if (!exame) return;
        exame.resultado = form(e).r;
        exame.status = "Concluída";
        save();
        toast("Resultado lançado!");
    }));

    $("#fPriv")?.addEventListener("submit", e => {
        e.preventDefault();
        if (!exige("paciente")) return;
        S.consent[usuario.nome] = new FormData(e.target).getAll("aut");
        save();
        toast("Autorizações salvas!");
    });

    document.querySelectorAll("[data-canc]").forEach(b => b.addEventListener("click", () => {
        if (!exige("paciente")) return;
        const ficha = S.fichas.find(f => f.id === b.dataset.canc && f.paciente === usuario.nome);
        if (!ficha) return;
        ficha.status = "Cancelada";
        save();
        toast("Ficha cancelada.");
    }));

    $("#busca")?.addEventListener("input", e => {
        $("#lu").innerHTML = listaUni(e.target.value);
    });

    $("#fFicha")?.addEventListener("submit", e => {
        e.preventDefault();
        S.fichas.unshift({
            ...form(e),
            ...(usuario.perfil === "paciente" ? { paciente: usuario.nome } : {}),
            id: novoId("F"),
            status: "Solicitada",
            data: hoje()
        });
        save();
        toast("Ficha solicitada!");
    });

    $("#fReg")?.addEventListener("submit", e => {
        e.preventDefault();
        S.registros.unshift({
            ...form(e),
            id: novoId("R"),
            data: hoje()
        });
        save();
        toast("Registro salvo!");
    });

    $("#fEnc")?.addEventListener("submit", e => {
        e.preventDefault();
        const dados = form(e);
        const autorizado = S.consent[dados.paciente];
        dados.comp = new FormData(e.target).getAll("comp").filter(c => !autorizado || autorizado.includes(c));

        S.enc.unshift({
            ...dados,
            id: novoId("E"),
            status: "Pendente",
            data: hoje()
        });

        save();
        toast("Encaminhamento enviado!");
    });

    document.querySelectorAll("[data-av]").forEach(botao => {
        botao.addEventListener("click", () => {
            const estados = ["Solicitada", "Em triagem", "Agendada", "Concluída"];
            const ficha = S.fichas.find(f => f.id === botao.dataset.av);

            if (!ficha) return;

            const indice = estados.indexOf(ficha.status);
            ficha.status = estados[Math.min(indice + 1, estados.length - 1)];

            save();
        });
    });

    document.querySelectorAll("[data-rec]").forEach(botao => {
        botao.addEventListener("click", () => {
            const encaminhamento = S.enc.find(e => e.id === botao.dataset.rec);
            if (!encaminhamento) return;

            encaminhamento.status = "Recebido";
            save();
            toast("Recebimento confirmado!");
        });
    });

    document.querySelectorAll("[data-ret]").forEach(f => {
        f.addEventListener("submit", e => {
            e.preventDefault();

            const encaminhamento = S.enc.find(x => x.id === f.dataset.ret);
            if (!encaminhamento) return;

            encaminhamento.status = "Atendido";
            encaminhamento.retorno = form(e).r;

            save();
            toast("Retorno registrado!");
        });
    });
}

/* INICIALIZAÇÃO */

window.addEventListener("hashchange", render);
render();
carregarUnidades().then(() => { if (usuario) render(); });
