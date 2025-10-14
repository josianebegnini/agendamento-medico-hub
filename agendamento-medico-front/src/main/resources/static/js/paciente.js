const API_URL = `${API_GATEWAY_BASE}/api/pacientes`;
const API_CONVENIOS = `${API_GATEWAY_BASE}/api/convenios`;

let pacientes = [];
let paginaAtual = 1;
const itensPorPagina = 10;
let pacienteEmEdicaoId = null;

document.addEventListener('DOMContentLoaded', () => {
    // ✅ Aguarda carregar token antes de iniciar as requisições
    verificarLogin();

    carregarPacientes();
    carregarConvenios();

    const form = document.getElementById('pacienteForm');
    if (form) form.addEventListener('submit', salvarPaciente);

    const cancelarBtn = document.getElementById('btnCancelar');
    if (cancelarBtn) cancelarBtn.addEventListener('click', cancelarEdicaoPaciente);

    document.getElementById('prevPage').addEventListener('click', () => mudarPagina(-1));
    document.getElementById('nextPage').addEventListener('click', () => mudarPagina(1));

    document.getElementById('telefonePaciente').addEventListener('input', aplicarMascaraTelefone);
});

// 🔒 Valida se o usuário está autenticado
function verificarLogin() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert('Você precisa estar logado!');
        window.location.href = '/login.html';
    }
}

// 🔐 Função padrão para headers autenticados
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    if (!token) {
        console.warn('⚠️ Token JWT não encontrado no localStorage!');
        alert('Você precisa estar logado!');
        window.location.href = '/login.html';
        throw new Error('Token ausente');
    }
    return {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    };
}

// 🧾 Carrega todos os pacientes
async function carregarPacientes() {
  const lista = document.getElementById('listaPacientes');
  lista.innerHTML = '<li>Carregando...</li>';

  try {
    const response = await fetch(API_URL, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!response.ok) throw new Error(`Erro HTTP ${response.status}`);

    pacientes = await response.json();
    exibirPacientes();

  } catch (error) {
    console.error('Erro ao carregar pacientes:', error);
    lista.innerHTML = '<li>Erro ao carregar pacientes.</li>';
  }
}


// 🏥 Carrega os convênios no select
async function carregarConvenios() {
    try {
        const response = await fetch(API_CONVENIOS, { headers: getAuthHeaders() });

        if (!response.ok) throw new Error(`Erro ${response.status}`);

        const convenios = await response.json();
        const select = document.getElementById('convenioPaciente');
        select.innerHTML = '<option value="">Selecione um convênio</option>';

        convenios.forEach(conv => {
            const option = document.createElement('option');
            option.value = conv.id;
            option.textContent = `${conv.nome} (${conv.cobertura})`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('❌ Erro ao carregar convênios:', error);
    }
}

// 🧍 Renderiza a lista de pacientes
function exibirPacientes() {
    const lista = document.getElementById('listaPacientes');
    lista.innerHTML = '';

    const inicio = (paginaAtual - 1) * itensPorPagina;
    const fim = inicio + itensPorPagina;
    const pacientesPagina = pacientes.slice(inicio, fim);

    if (pacientesPagina.length === 0) {
        lista.innerHTML = '<li>Nenhum paciente cadastrado.</li>';
    } else {
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
    }

    const totalPaginas = Math.ceil(pacientes.length / itensPorPagina);
    document.getElementById('pageInfo').textContent =
        `Página ${paginaAtual} de ${totalPaginas || 1}`;
}

// 🔁 Paginação simples
function mudarPagina(direcao) {
    const totalPaginas = Math.ceil(pacientes.length / itensPorPagina);
    if (paginaAtual + direcao >= 1 && paginaAtual + direcao <= totalPaginas) {
        paginaAtual += direcao;
        exibirPacientes();
    }
}

// 💾 Salvar ou atualizar paciente
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
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_URL}/${id}` : API_URL;

        const response = await fetch(url, {
            method,
            headers: getAuthHeaders(),
            body: JSON.stringify(paciente)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao salvar paciente');
        }

        alert(id ? 'Paciente atualizado com sucesso!' : 'Paciente salvo com sucesso!');
        cancelarEdicaoPaciente();
        await carregarPacientes();

    } catch (error) {
        console.error('❌ Erro ao salvar paciente:', error);
        alert(error.message || 'Erro ao salvar paciente!');
    }
}

// ✏️ Edição de paciente
function editar(id) {
    const paciente = pacientes.find(p => p.id === id);
    if (!paciente) return;
    iniciarEdicaoPaciente(paciente);
}

// ❌ Excluir paciente
async function deletarPaciente(id) {
    if (!confirm('Deseja realmente excluir este paciente?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Erro ao excluir paciente');
        }

        alert('Paciente excluído com sucesso!');
        await carregarPacientes();

    } catch (error) {
        console.error('❌ Erro ao excluir paciente:', error);
        alert(error.message || 'Erro ao excluir paciente!');
    }
}

// 🧩 Preenche o formulário para edição
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

// 🧹 Reset formulário
function resetFormularioPaciente() {
    const form = document.getElementById('pacienteForm');
    if (form) form.reset();
    document.getElementById('idPaciente').value = '';
}

// 🚫 Cancela edição
function cancelarEdicaoPaciente() {
    pacienteEmEdicaoId = null;

    document.getElementById('formTitle').textContent = 'Novo Paciente';
    document.getElementById('btnSalvar').textContent = 'Salvar Paciente';
    document.getElementById('btnCancelar').classList.add('hidden');

    resetFormularioPaciente();
}

// ☎️ Máscara telefone
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
