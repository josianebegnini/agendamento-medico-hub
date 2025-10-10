const API_URL = `${API_GATEWAY_BASE}/api/especialidades`;

let currentPage = 1;
const pageSize = 10;
let especialidades = [];
let especialidadeEmEdicaoId = null;

document.addEventListener('DOMContentLoaded', () => {
    verificarLogin();
    carregarEspecialidades();

    const form = document.getElementById('especialidadeForm');
    if (form) form.addEventListener('submit', salvarEspecialidade);

    const cancelarBtn = document.getElementById('btnCancelar');
    if (cancelarBtn) cancelarBtn.addEventListener('click', cancelarEdicaoEspecialidade);

    document.getElementById('prevPage').addEventListener('click', () => mudarPagina(-1));
    document.getElementById('nextPage').addEventListener('click', () => mudarPagina(1));
});

function verificarLogin() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Você precisa estar logado!');
        window.location.href = '/login.html';
        throw new Error('Token JWT ausente.');
    }
}

function getAuthHeaders() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Você precisa estar logado!');
        window.location.href = '/login.html';
        throw new Error('Token ausente');
    }

    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}

// 🔹 Buscar todas as especialidades
async function carregarEspecialidades() {
    const lista = document.getElementById('listaEspecialidades');
    lista.innerHTML = '<li>Carregando...</li>';

    try {
        const response = await fetch(API_URL, {
            method: 'GET',
            mode: 'cors',
            credentials: 'include',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error(`Erro HTTP ${response.status}`);

        especialidades = await response.json();
        renderizarLista();

    } catch (error) {
        console.error('❌ Erro ao carregar especialidades:', error);
        lista.innerHTML = '<li>Erro ao carregar especialidades</li>';
    }
}

function renderizarLista() {
    const lista = document.getElementById('listaEspecialidades');
    lista.innerHTML = '';

    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    const pagina = especialidades.slice(start, end);

    if (pagina.length === 0) {
        lista.innerHTML = '<li>Nenhuma especialidade cadastrada.</li>';
    } else {
        pagina.forEach(esp => {
            const li = document.createElement('li');
            li.innerHTML = `
                <span>${esp.id} - ${esp.nome}</span>
                <div>
                    <button class="btn-action" onclick="editar(${esp.id}, '${esp.nome}')">Editar</button>
                    <button class="btn-action" onclick="deletarEspecialidade(${esp.id})">Excluir</button>
                </div>
            `;
            lista.appendChild(li);
        });
    }

    atualizarPaginacao();
}

function atualizarPaginacao() {
    const totalPaginas = Math.ceil(especialidades.length / pageSize);
    document.getElementById('pageInfo').textContent = `Página ${currentPage} de ${totalPaginas || 1}`;
    document.getElementById('prevPage').disabled = currentPage === 1;
    document.getElementById('nextPage').disabled = currentPage === totalPaginas || totalPaginas === 0;
}

function mudarPagina(delta) {
    currentPage += delta;
    renderizarLista();
}

// 💾 Criar ou atualizar
async function salvarEspecialidade(event) {
    event.preventDefault();

    const id = especialidadeEmEdicaoId;
    const nome = document.getElementById('nomeEspecialidade').value.trim();

    if (!nome) {
        alert('Informe o nome da especialidade!');
        return;
    }

    const especialidade = { nome };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_URL}/${id}` : API_URL;

        const response = await fetch(url, {
            method,
            mode: 'cors',
            credentials: 'include',
            headers: getAuthHeaders(),
            body: JSON.stringify(especialidade)
        });

        if (!response.ok) throw new Error(`Erro ${response.status}`);

        alert(id ? 'Especialidade atualizada com sucesso!' : 'Especialidade salva com sucesso!');
        cancelarEdicaoEspecialidade();
        await carregarEspecialidades();

    } catch (error) {
        console.error('❌ Erro ao salvar especialidade:', error);
        alert('Erro ao salvar a especialidade!');
    }
}

// ✏️ Editar
function editar(id, nome) {
    especialidadeEmEdicaoId = id;
    document.getElementById('formTitle').textContent = 'Editar Especialidade';
    document.getElementById('btnSalvar').textContent = 'Atualizar Especialidade';
    document.getElementById('btnCancelar').classList.remove('hidden');

    document.getElementById('idEspecialidade').value = id;
    document.getElementById('nomeEspecialidade').value = nome;
}

// ❌ Excluir
async function deletarEspecialidade(id) {
    if (!confirm('Deseja realmente excluir esta especialidade?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE',
            mode: 'cors',
            credentials: 'include',
            headers: getAuthHeaders()
        });

        if (!response.ok) throw new Error(`Erro ${response.status}`);

        alert('Especialidade excluída com sucesso!');
        await carregarEspecialidades();

    } catch (error) {
        console.error('❌ Erro ao excluir especialidade:', error);
        alert('Erro ao excluir a especialidade!');
    }
}

// 🔄 Resetar formulário
function resetFormularioEspecialidade() {
    const form = document.getElementById('especialidadeForm');
    if (form) form.reset();
    document.getElementById('idEspecialidade').value = '';
}

// 🚫 Cancelar edição
function cancelarEdicaoEspecialidade() {
    especialidadeEmEdicaoId = null;
    document.getElementById('formTitle').textContent = 'Nova Especialidade';
    document.getElementById('btnSalvar').textContent = 'Salvar Especialidade';
    document.getElementById('btnCancelar').classList.add('hidden');
    resetFormularioEspecialidade();
}
