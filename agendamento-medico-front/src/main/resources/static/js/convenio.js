const API_URL = `${API_GATEWAY_BASE}/api/convenios`;

let currentPage = 1;
const pageSize = 5;
let convenios = [];
let convenioEmEdicaoId = null;

document.addEventListener('DOMContentLoaded', () => {
    carregarConvenios();

    const form = document.getElementById('convenioForm');
    if (form) {
        form.addEventListener('submit', salvarConvenio);
    }

    const cancelarBtn = document.getElementById('btnCancelar');
    if (cancelarBtn) {
        cancelarBtn.addEventListener('click', cancelarEdicaoConvenio);
    }

    document.getElementById('prevPage').addEventListener('click', () => mudarPagina(-1));
    document.getElementById('nextPage').addEventListener('click', () => mudarPagina(1));

    const telefoneInput = document.getElementById('telefoneConvenio');
    telefoneInput.addEventListener('input', aplicarMascaraTelefone);
});

// 📞 Máscara de telefone
function aplicarMascaraTelefone(event) {
    let valor = event.target.value.replace(/\D/g, '');
    if (valor.length > 11) valor = valor.slice(0, 11);

    if (valor.length <= 10) {
        valor = valor.replace(/^(\d{2})(\d{4})(\d{0,4})$/, '($1)$2-$3');
    } else {
        valor = valor.replace(/^(\d{2})(\d{5})(\d{0,4})$/, '($1)$2-$3');
    }

    event.target.value = valor;
}

// 📥 Carregar convênios
async function carregarConvenios() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Você precisa estar logado!');
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch(`${API_URL}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) {
            throw new Error('Erro ao carregar convênios');
        }

        convenios = await response.json();
        renderizarLista();
    } catch (err) {
        console.error('Erro ao carregar convenios:', err);
        const lista = document.getElementById('listaConvenios');
        lista.innerHTML = '<li>Erro ao carregar convênios.</li>';
    }
}

// 🧾 Renderizar lista paginada
function renderizarLista() {
    const lista = document.getElementById('listaConvenios');
    lista.innerHTML = '';

    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    const pagina = convenios.slice(start, end);

    if (pagina.length === 0) {
        lista.innerHTML = '<li>Nenhum convênio cadastrado.</li>';
    } else {
        pagina.forEach(c => {
            const li = document.createElement('li');
            li.innerHTML = `
                <span>${c.id} - ${c.nome} (${c.cobertura})</span>
                <div>
                    <button class="btn-action" onclick="editar(${c.id}, '${c.nome}', '${c.cobertura}', '${c.telefoneContato || ''}')">Editar</button>
                    <button class="btn-action" onclick="deletarConvenio(${c.id})">Excluir</button>
                </div>
            `;
            lista.appendChild(li);
        });
    }

    atualizarPaginacao();
}

// 📄 Atualizar paginação
function atualizarPaginacao() {
    const totalPaginas = Math.ceil(convenios.length / pageSize);
    document.getElementById('pageInfo').textContent = `Página ${currentPage} de ${totalPaginas || 1}`;

    document.getElementById('prevPage').disabled = currentPage === 1;
    document.getElementById('nextPage').disabled = currentPage === totalPaginas || totalPaginas === 0;
}

// 🔁 Mudar página
function mudarPagina(delta) {
    currentPage += delta;
    renderizarLista();
}

// 💾 Criar ou atualizar convênio
async function salvarConvenio(event) {
    event.preventDefault();

    const id = convenioEmEdicaoId;
    const nome = document.getElementById('nomeConvenio').value.trim();
    const cobertura = document.getElementById('coberturaConvenio').value.trim();
    let telefoneContato = document.getElementById('telefoneConvenio').value.replace(/\D/g, '');
    const token = localStorage.getItem('token');

    if (!nome || !cobertura) {
        alert('Preencha todos os campos obrigatórios!');
        return;
    }

    const convenio = { nome, cobertura, telefoneContato };

    try {
        const url = id ? `${API_URL}/${id}` : API_URL;
        const method = id ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(convenio)
        });

        if (!response.ok) throw new Error('Erro ao salvar o convênio');

        alert(id ? 'Convênio atualizado com sucesso!' : 'Convênio salvo com sucesso!');
        cancelarEdicaoConvenio();
        await carregarConvenios();

    } catch (error) {
        console.error(error);
        alert('Erro ao salvar o convênio!');
        if (convenioEmEdicaoId) cancelarEdicaoConvenio();
        else resetFormularioConvenio();
    }
}

// ✏️ Editar convênio
function editar(id, nome, cobertura, telefoneContato) {
    convenioEmEdicaoId = id;

    document.getElementById('formTitle').textContent = 'Editar Convênio';
    document.getElementById('btnSalvar').textContent = 'Atualizar Convênio';
    document.getElementById('btnCancelar').classList.remove('hidden');

    document.getElementById('idConvenio').value = id;
    document.getElementById('nomeConvenio').value = nome;
    document.getElementById('coberturaConvenio').value = cobertura;
    document.getElementById('telefoneConvenio').value = telefoneContato || '';
}

// 🗑️ Deletar convênio
async function deletarConvenio(id) {
    if (!confirm('Deseja realmente excluir este convênio?')) return;

    const token = localStorage.getItem('token');

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error('Erro ao excluir o convênio');

        await carregarConvenios();
        alert('Convênio excluído com sucesso!');
    } catch (error) {
        console.error(error);
        alert('Erro ao excluir o convênio!');
    }
}

// 🔄 Resetar formulário
function resetFormularioConvenio() {
    const form = document.getElementById('convenioForm');
    if (form) form.reset();
    document.getElementById('idConvenio').value = '';
}

// ❌ Cancelar edição
function cancelarEdicaoConvenio() {
    convenioEmEdicaoId = null;

    document.getElementById('formTitle').textContent = 'Novo Convênio';
    document.getElementById('btnSalvar').textContent = 'Salvar Convênio';
    document.getElementById('btnCancelar').classList.add('hidden');

    resetFormularioConvenio();
}
