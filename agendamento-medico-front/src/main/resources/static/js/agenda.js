const API_PACIENTES = `${API_GATEWAY_BASE}/api/pacientes`;
const API_MEDICOS   = `${API_GATEWAY_BASE}/api/medicos`;
const API_AGENDAS   = `${API_GATEWAY_BASE}/api/agendas`;

let agendamentos = [];
let paginaAtual = 1;
const itensPorPagina = 5;

let pacientesCache = [];
let medicosCache = [];

// 🔒 Garante login antes de carregar dados
document.addEventListener('DOMContentLoaded', () => {
  verificarLogin();

  carregarPacientes();
  carregarMedicos();
  carregarAgendamentos();

  document.getElementById('agendaForm').addEventListener('submit', agendarConsulta);
  document.getElementById('btnCancelarEdicao').addEventListener('click', cancelarEdicao);
  document.getElementById('prevPage').addEventListener('click', () => mudarPagina(-1));
  document.getElementById('nextPage').addEventListener('click', () => mudarPagina(1));
});

// ✅ Verifica token no localStorage
function verificarLogin() {
  const token = localStorage.getItem('token');
  if (!token) {
    alert('Você precisa estar logado!');
    window.location.href = '/login.html';
    throw new Error('Token JWT ausente.');
  }
}

// ✅ Headers autenticados
function getAuthHeaders() {
  const token = localStorage.getItem('token');
  if (!token) throw new Error('Token ausente');
  return {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  };
}

// 🧍‍♀️ Carrega pacientes
async function carregarPacientes() {
  const select = document.getElementById('paciente');
  select.innerHTML = '<option value="">Carregando pacientes...</option>';

  try {
    const res = await fetch(API_PACIENTES, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);
    const data = await res.json();
    const pacientes = Array.isArray(data) ? data : (data.content || []);
    pacientesCache = pacientes;

    if (!pacientes.length) {
      select.innerHTML = '<option value="">Nenhum paciente cadastrado</option>';
      select.disabled = true;
      return;
    }

    select.disabled = false;
    select.innerHTML = '<option value="">Selecione um paciente</option>';

    pacientes.forEach(p => {
      const op = document.createElement('option');
      op.value = p.id;
      op.textContent = `${p.nome}${p.email ? ` (${p.email})` : ''}`;
      select.appendChild(op);
    });

  } catch (err) {
    console.error('❌ Erro ao carregar pacientes:', err);
    select.innerHTML = '<option value="">Erro ao carregar pacientes</option>';
    select.disabled = true;
  }
}

// 👨‍⚕️ Carrega médicos
async function carregarMedicos() {
  const select = document.getElementById('medico');
  select.innerHTML = '<option value="">Carregando médicos...</option>';

  try {
    const res = await fetch(API_MEDICOS, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);
    const data = await res.json();
    const medicos = Array.isArray(data) ? data : (data.content || []);
    medicosCache = medicos;

    if (!medicos.length) {
      select.innerHTML = '<option value="">Nenhum médico cadastrado</option>';
      select.disabled = true;
      return;
    }

    select.disabled = false;
    select.innerHTML = '<option value="">Selecione um médico</option>';

    medicos.forEach(m => {
      const op = document.createElement('option');
      op.value = m.id;
      const especialidades = Array.isArray(m.especialidades)
        ? m.especialidades.map(e => e.nome ?? e).join(', ')
        : (m.especialidades || '');
      op.textContent = especialidades ? `${m.nome} - ${especialidades}` : m.nome;
      select.appendChild(op);
    });

  } catch (err) {
    console.error('❌ Erro ao carregar médicos:', err);
    select.innerHTML = '<option value="">Erro ao carregar médicos</option>';
    select.disabled = true;
  }
}

// 📅 Carrega agendamentos
async function carregarAgendamentos() {
  const lista = document.getElementById('listaAgendamentos');
  lista.innerHTML = '<li>Carregando...</li>';

  try {
    const res = await fetch(API_AGENDAS, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);
    const data = await res.json();
    agendamentos = Array.isArray(data) ? data : (data.content || []);
    exibirAgendamentos();

  } catch (err) {
    console.error('❌ Erro ao carregar agendamentos:', err);
    lista.innerHTML = '<li>Erro ao carregar agendamentos</li>';
  }
}

function exibirAgendamentos() {
  const lista = document.getElementById('listaAgendamentos');
  lista.innerHTML = '';

  const inicio = (paginaAtual - 1) * itensPorPagina;
  const fim = inicio + itensPorPagina;
  const pagina = agendamentos.slice(inicio, fim);

  if (!pagina.length) {
    lista.innerHTML = '<li>Nenhum agendamento encontrado.</li>';
    document.getElementById('pageInfo').textContent = '';
    return;
  }

  pagina.forEach(a => {
    const li = document.createElement('li');
    li.innerHTML = `
      <div>
        <strong>${a.pacienteNome}</strong> - ${a.medicoNome}<br>
        <small>${formatarDataParaLista(a.dataHora)}</small> |
        <em>${a.tipoConsulta}</em> |
        <span class="status status-${(a.status || 'AGENDADA').toLowerCase()}">${traduzirStatus(a.status)}</span>
      </div>
      <div class="list-actions">
        <button class="btn-action" onclick="prepararEdicao(${a.id})">Editar</button>
        <button class="btn-action btn-action-danger" onclick="cancelarAgendamento(${a.id})">Cancelar</button>
      </div>
    `;
    lista.appendChild(li);
  });

  const totalPaginas = Math.ceil(agendamentos.length / itensPorPagina);
  document.getElementById('pageInfo').textContent = `Página ${paginaAtual} de ${totalPaginas}`;
}

function mudarPagina(direcao) {
  const totalPaginas = Math.ceil(agendamentos.length / itensPorPagina);
  if (paginaAtual + direcao >= 1 && paginaAtual + direcao <= totalPaginas) {
    paginaAtual += direcao;
    exibirAgendamentos();
  }
}

async function agendarConsulta(event) {
  event.preventDefault();

  const id = document.getElementById('agendamentoId').value;
  const pacienteId = document.getElementById('paciente').value;
  const medicoId = document.getElementById('medico').value;
  const tipoConsulta = document.getElementById('tipoConsulta').value;
  const dataHora = document.getElementById('dataHora').value;

  if (!pacienteId || !medicoId || !dataHora) {
    alert('Preencha todos os campos.');
    return;
  }

    const dataSelecionada = new Date(dataHora);
    const agora = new Date();

    if (dataSelecionada < agora) {
      alert('❌ Não pode agendar com uma data anterior à data de hoje.');
      return;
    }

  const payload = {
    pacienteId: Number(pacienteId),
    medicoId: Number(medicoId),
    tipoConsulta,
    dataHora
  };

  try {
    const url = id
      ? `${API_AGENDAS}/${id}`  // se tiver ID, faz update
      : `${API_AGENDAS}/agendar`;

    const method = id ? 'PUT' : 'POST';

    const res = await fetch(url, {
      method,
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload)
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);

    alert(id ? 'Agendamento atualizado com sucesso!' : 'Consulta agendada com sucesso!');
    resetFormulario();
    carregarAgendamentos();

  } catch (err) {
    console.error('❌ Erro ao salvar agendamento:', err);
    alert('Erro ao salvar agendamento.');
  }
}

// ❌ Cancelar agendamento
async function cancelarAgendamento(id) {
  if (!confirm('Deseja cancelar este agendamento?')) return;

  try {
    const res = await fetch(`${API_AGENDAS}/${id}/cancelar`, {
      method: 'PATCH',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);
    alert('Agendamento cancelado com sucesso!');
    carregarAgendamentos();

  } catch (err) {
    console.error('❌ Erro ao cancelar agendamento:', err);
    alert('Erro ao cancelar agendamento.');
  }
}

function resetFormulario() {
  const form = document.getElementById("agendaForm");
  form.reset();

  // 🔓 Reabilita selects para novo agendamento
  document.getElementById("paciente").disabled = false;
  document.getElementById("medico").disabled = false;

  document.getElementById("btnCancelarEdicao").classList.add("hidden");
  document.getElementById("btnAgendar").textContent = "Agendar";
}

function traduzirStatus(status) {
  const mapa = { AGENDADA: 'Agendada', CANCELADA: 'Cancelada', CONCLUIDA: 'Concluída' };
  return mapa[status] || status || '';
}

function formatarDataParaLista(dataHora) {
  if (!dataHora) return '';
  const data = new Date(dataHora);
  return data.toLocaleString('pt-BR');
}

function cancelarEdicao() {
  resetFormulario();
}

function prepararEdicao(id) {
  const agendamento = agendamentos.find(a => a.id === id);
  if (!agendamento) {
    alert("Agendamento não encontrado.");
    return;
  }

  // Extrai IDs
  const pacienteId = agendamento.pacienteId || "";
  const medicoId = agendamento.medicoId || "";

  // Preenche o formulário
  document.getElementById("agendamentoId").value = agendamento.id;
  document.getElementById("paciente").value = pacienteId;
  document.getElementById("medico").value = medicoId;
  document.getElementById("tipoConsulta").value = agendamento.tipoConsulta || "PRESENCIAL";

  if (agendamento.dataHora) {
    const data = new Date(agendamento.dataHora);
    document.getElementById("dataHora").value = data.toISOString().slice(0, 16);
  }

  // 🔒 Desabilita campos que não devem ser alterados
  document.getElementById("paciente").disabled = true;
  document.getElementById("medico").disabled = true;

  // Atualiza botões
  document.getElementById("btnAgendar").textContent = "Salvar alterações";
  document.getElementById("btnCancelarEdicao").classList.remove("hidden");
}


