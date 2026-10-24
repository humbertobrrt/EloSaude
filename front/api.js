/* Cliente da API EloSaúde (documentada em http://localhost:8080/swagger-ui/index.html) */
const API_URL = "http://localhost:8080";
const CHAVE_TOKEN = "elosaude_token";
const CHAVE_USER = "elosaude_user";

/* Perfil do backend -> perfil usado nas telas do front */
const PERFIL_FRONT = { PACIENTE: "paciente", PROFISSIONAL: "medico", ADMIN: "medico" };

const Sessao = {
    token: () => sessionStorage.getItem(CHAVE_TOKEN),
    usuario() {
        try { return JSON.parse(sessionStorage.getItem(CHAVE_USER)); }
        catch { return null; }
    },
    salvar(token, usuarioApi) {
        const u = {
            id: usuarioApi.id,
            email: usuarioApi.email,
            nome: usuarioApi.nome,
            perfilApi: usuarioApi.perfil,
            perfil: PERFIL_FRONT[usuarioApi.perfil] || "paciente",
            unidadeId: null // o backend ainda não vincula usuário a unidade
        };
        sessionStorage.setItem(CHAVE_TOKEN, token);
        sessionStorage.setItem(CHAVE_USER, JSON.stringify(u));
        return u;
    },
    limpar() {
        sessionStorage.removeItem(CHAVE_TOKEN);
        sessionStorage.removeItem(CHAVE_USER);
    }
};

class ApiError extends Error {
    constructor(status, message) { super(message); this.status = status; }
}

async function api(caminho, { metodo = "GET", corpo, auth = true } = {}) {
    const headers = { "Content-Type": "application/json" };

    // Só adiciona o token se auth for true E existir um token válido
    if (auth) {
        const token = Sessao.token();
        if (token) {
            headers.Authorization = "Bearer " + token;
        }
    }

    let resp;
    try {
        resp = await fetch(API_URL + caminho, {
            method: metodo,
            headers,
            body: corpo ? JSON.stringify(corpo) : undefined
        });
    } catch {
        throw new ApiError(0, "Não foi possível conectar ao servidor. Verifique se a API está rodando em " + API_URL + ".");
    }

    if (resp.status === 401 && auth) {
        Sessao.limpar();
        if (!location.pathname.endsWith("login.html")) location.href = "login.html";
    }

    const dados = await resp.json().catch(() => null);
    if (!resp.ok) {
        const msg = dados?.message || dados?.error || dados?.detail || ("Erro " + resp.status);
        throw new ApiError(resp.status, msg);
    }
    return dados;
}

const Api = {
    // --- Autenticação e Usuário Atual ---
    login: (email, senha) => api("/api/auth/login", { metodo: "POST", corpo: { email, senha }, auth: false }),
    registrar: (nome, email, senha) => {
        // Garante que qualquer sessão residual seja limpa antes de registrar
        Sessao.limpar();
        return api("/api/usuarios/registro", { metodo: "POST", corpo: { nome, email, senha }, auth: false });
    },
    eu: () => api("/api/usuarios/me"),
    atualizarMeuNome: nome => api("/api/usuarios/me", { metodo: "PATCH", corpo: { nome } }),

    // --- Gerenciamento de Usuários (somente ADMIN) ---
    listarUsuarios: () => api("/api/usuarios"),
    criarUsuario: dados => api("/api/usuarios", { metodo: "POST", corpo: dados }),
    buscarUsuario: id => api("/api/usuarios/" + id),
    alterarAtivo: (id, valor) => api(`/api/usuarios/${id}/ativo?valor=${valor}`, { metodo: "PATCH" }),

    // --- Unidades de Saúde ---
    listarUnidades: () => api("/api/unidades-saude"),
    buscarUnidade: id => api("/api/unidades-saude/" + id),
    criarUnidade: dados => api("/api/unidades-saude", { metodo: "POST", corpo: dados }),
    atualizarUnidade: (id, dados) => api("/api/unidades-saude/" + id, { metodo: "PUT", corpo: dados }),
    excluirUnidade: id => api("/api/unidades-saude/" + id, { metodo: "DELETE" }),
    buscarPorCnes: cnes => api("/api/unidades-saude/cnes/" + cnes),
    alterarUnidadeAtiva: (id, valor) => api(`/api/unidades-saude/${id}/ativa`, { metodo: "PATCH", corpo: { ativa: valor } })
};

/* Login completo: autentica, guarda o token e devolve o usuário da sessão */
async function entrar(email, senha) {
    const r = await Api.login(email.trim(), senha);
    return Sessao.salvar(r.token, r.usuario);
}