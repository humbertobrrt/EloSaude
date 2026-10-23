/* Contas de demonstração (apenas para o hackathon; não é autenticação real) */
const CONTAS = [
    { email: "paciente@elosaude.com.br", senha: "Paciente123!", nome: "Maria Souza", perfil: "paciente", unidadeId: "u1" },
    { email: "medico@elosaude.com.br", senha: "Medico123!", nome: "Dra. Ana Ribeiro", perfil: "medico", unidadeId: "u1" }
];

function autenticar(email, senha) {
    const conta = CONTAS.find(c => c.email === email.trim().toLowerCase() && c.senha === senha);
    if (!conta) return null;
    const { senha: _, ...perfil } = conta;
    return perfil;
}

const salvarSessao = perfil => sessionStorage.setItem("elosaude_user", JSON.stringify(perfil));

function lerSessao() {
    try { return JSON.parse(sessionStorage.getItem("elosaude_user")); }
    catch { return null; }
}
