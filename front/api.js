/* unidades.js — carrega as unidades da API e alimenta os cards e o select.
 *
 * Requer o api.js carregado ANTES (usa Api.listarUnidades()).
 *
 * HTML esperado (ajuste os ids se os seus forem outros):
 *   <input id="busca-unidades" placeholder="Buscar por nome, bairro ou especialidade...">
 *   <div id="lista-unidades"></div>
 *   <select id="unidade-select"></select>
 *
 *   <script src="api.js"></script>
 *   <script src="unidades.js"></script>
 */

let unidadesCache = [];

/* Isola os nomes dos campos da API num lugar só.
 * A API devolve: id, nome, tipo, endereco, bairro, cidade, uf, telefone,
 * especialidades (string separada por vírgula) e ativa. */
function normalizar(u) {
    return {
        id: u.id,
        nome: u.nome || "",
        tipo: u.tipo || "",
        endereco: u.endereco || "",
        bairro: u.bairro || "",
        cidade: u.cidade || "",
        uf: u.uf || "",
        telefone: u.telefone || "",
        // "Clínica geral, Enfermagem, Pediatria" -> ["Clínica geral", "Enfermagem", "Pediatria"]
        especialidades: typeof u.especialidades === "string"
            ? u.especialidades.split(",").map(e => e.trim()).filter(Boolean)
            : Array.isArray(u.especialidades) ? u.especialidades : [],
        ativa: u.ativa !== false
    };
}

/* Evita injeção de HTML vindo da API */
function esc(texto) {
    const d = document.createElement("div");
    d.textContent = texto ?? "";
    return d.innerHTML;
}

function renderCards(lista) {
    const container = document.querySelector("#lista-unidades");
    if (!container) return;

    if (!lista.length) {
        container.innerHTML = "<p>Nenhuma unidade encontrada.</p>";
        return;
    }

    container.innerHTML = lista.map(u => `
        <article class="card-unidade">
            <span class="tag">${esc(u.tipo)}</span>
            <h3>${esc(u.nome)}</h3>
            <p>${esc(u.bairro)}${u.cidade ? " · " + esc(u.cidade) : ""}</p>
            ${u.telefone ? `<p class="telefone">${esc(u.telefone)}</p>` : ""}
            <div class="tags">
                ${u.especialidades.map(e => `<span class="tag">${esc(e)}</span>`).join("")}
            </div>
        </article>
    `).join("");
}

function renderSelect(lista) {
    const select = document.querySelector("#unidade-select");
    if (!select) return;

    // value = id da unidade (é o que vai para o backend em "Solicitar ficha")
    select.innerHTML = lista
        .map(u => `<option value="${esc(String(u.id))}">${esc(u.nome)}</option>`)
        .join("");
}

/* Busca por nome, bairro ou especialidade (filtra o que já foi carregado) */
function filtrar(termo) {
    const t = termo.trim().toLowerCase();
    if (!t) return unidadesCache;
    return unidadesCache.filter(u =>
        u.nome.toLowerCase().includes(t) ||
        u.bairro.toLowerCase().includes(t) ||
        u.especialidades.some(e => e.toLowerCase().includes(t))
    );
}

async function carregarUnidades() {
    const container = document.querySelector("#lista-unidades");
    if (container) container.innerHTML = "<p>Carregando unidades...</p>";

    try {
        const dados = await Api.listarUnidades();
        // aceita array puro ou resposta paginada ({ content: [...] })
        const lista = Array.isArray(dados) ? dados : (dados?.content || []);
        unidadesCache = lista.map(normalizar).filter(u => u.ativa);

        renderCards(unidadesCache);
        renderSelect(unidadesCache);
    } catch (e) {
        if (container) container.innerHTML = `<p class="erro">${esc(e.message)}</p>`;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    carregarUnidades();

    const busca = document.querySelector("#busca-unidades");
    if (busca) busca.addEventListener("input", () => renderCards(filtrar(busca.value)));
});