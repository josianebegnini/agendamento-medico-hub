const API_URL = `${API_BASE_URL}/api/pacientes`;
const API_CONVENIOS = `${API_BASE_URL}/api/convenios`;

let pacientes = [];
let paginaAtual = 1;
const itensPorPagina = 10;
let pacienteEmEdicaoId = null;

document.addEventListener('DOMContentLoaded', () => {
    carregarPacientes();
    carregarConvenios();

    const form = document.getElementById('pacienteForm');
    if (form) {
        form.addEventListener('submit', salvarPaciente);
    }

    const cancelarBtn = document.getElementById('btnCancelar');
    if (cancelarBtn) {
        cancelarBtn.addEventListener('click', cancelarEdicaoPaciente);
    }

    document.getElementById('prevPage').addEventListener('click', () => mudarPagina(-1));
    document.getElementById('nextPage').addEventListener('click', () => mudarPagina(1));

    document.getElementById('telefonePaciente').addEventListener('input', aplicarMascaraTelefone);
});

async function carregarPacientes() {
    const lista = document.getElementById('listaPacientes');
    lista.innerHTML = '<li>Carregando...</li>';

    try {
        const response = await fetch(API_URL);
        pacientes = await response.json();
        exibirPacientes();
    } catch (error) {
        lista.innerHTML = '<li>Erro ao carregar pacientes</li>';
        console.error(error);
    }
}

async function carregarConvenios() {
    try {
        const response = await fetch(API_CONVENIOS);
        const convenios = await response.json();

        const select = document.getElementById('convenioPaciente');
        convenios.forEach(conv => {
            const option = document.createElement('option');
            option.value = conv.id;
            option.textContent = `${conv.nome} (${conv.cobertura})`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Erro ao carregar convênios', error);
    }
}

function exibirPacientes() {
    const lista = document.getElementById('listaPacientes');
    lista.innerHTML = '';

    const inicio = (paginaAtual - 1) * itensPorPagina;
    const fim = inicio + itensPorPagina;
    const pacientesPagina = pacientes.slice(inicio, fim);

    pacientesPagina.forEach(p => {
        const li = document.createElement('li');
        li.innerHTML = `
            <span class="long-text">${p.id} - ${p.nome} (${p.email})</span>
            <div>
                <button class="btn-action" onclick="editar(${p.id})">Editar</button>
                <button class="btn-action" onclick="deletarPaciente(${p.id})">Excluir</button>
            </div>
        `;
        lista.appendChild(li);
    });

    if (pacientesPagina.length === 0) {
        lista.innerHTML = '<li>Nenhum paciente cadastrado.</li>';
    }

    document.getElementById('pageInfo').textContent =
        `Página ${paginaAtual} de ${Math.ceil(pacientes.length / itensPorPagina)}`;
}

function mudarPagina(direcao) {
    const totalPaginas = Math.ceil(pacientes.length / itensPorPagina);
    if (paginaAtual + direcao >= 1 && paginaAtual + direcao <= totalPaginas) {
        paginaAtual += direcao;
        exibirPacientes();
    }
}

async function salvarPaciente(event) {
    event.preventDefault();

    const id = pacienteEmEdicaoId;
    const nome = document.getElementById('nomePaciente').value.trim();
    const email = document.getElementById('emailPaciente').value.trim();
    const telefone = document.getElementById('telefonePaciente').value.replace(/\D/g, '');
    const dataNascimento = document.getElementById('dataNascimentoPaciente').value;
    const convenioId = document.getElementById('convenioPaciente').value || null;

    if (!nome || !email || !dataNascimento) {
        alert('Preencha todos os campos obrigatórios!');
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        alert('Por favor, insira um e-mail válido!');
        return;
    }

    const paciente = { nome, email, telefone, dataNascimento, convenioId };

    try {
        if (id) {
            await fetch(`${API_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(paciente)
            });
        } else {
            await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(paciente)
            });
        }

        alert(id ? 'Paciente atualizado com sucesso!' : 'Paciente salvo com sucesso!');
        cancelarEdicaoPaciente();
        carregarPacientes();

    } catch (error) {
        console.error(error);
        alert('Erro ao salvar paciente!');
        if (pacienteEmEdicaoId) {
            cancelarEdicaoPaciente();
        } else {
            resetFormularioPaciente();
        }
    }
}

function editar(id) {
    const paciente = pacientes.find(p => p.id === id);
    if (!paciente) return;

    iniciarEdicaoPaciente(paciente);
}

async function deletarPaciente(id) {
    if (!confirm('Deseja realmente excluir este paciente?')) return;

    try {
        await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        alert('Paciente excluído com sucesso!');
        carregarPacientes();
    } catch (error) {
        console.error(error);
        alert('Erro ao excluir paciente!');
    }
}

function iniciarEdicaoPaciente(paciente) {
    pacienteEmEdicaoId = paciente.id;

    document.getElementById('formTitle').textContent = 'Editar Paciente';
    document.getElementById('btnSalvar').textContent = 'Atualizar Paciente';
    document.getElementById('btnCancelar').classList.remove('hidden');

    document.getElementById('idPaciente').value = paciente.id;
    document.getElementById('nomePaciente').value = paciente.nome || '';
    document.getElementById('emailPaciente').value = paciente.email || '';
    document.getElementById('telefonePaciente').value = paciente.telefone || '';
    document.getElementById('dataNascimentoPaciente').value = paciente.dataNascimento || '';
    document.getElementById('convenioPaciente').value = paciente.convenio ? paciente.convenio.id : '';
}

function resetFormularioPaciente() {
    const form = document.getElementById('pacienteForm');
    if (form) {
        form.reset();
    }
    document.getElementById('idPaciente').value = '';
}

function cancelarEdicaoPaciente() {
    pacienteEmEdicaoId = null;

    document.getElementById('formTitle').textContent = 'Novo Paciente';
    document.getElementById('btnSalvar').textContent = 'Salvar Paciente';
    document.getElementById('btnCancelar').classList.add('hidden');

    resetFormularioPaciente();
}

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
