const API_MEDICOS = `${API_GATEWAY_BASE}/api/medicos`;
const API_ESPECIALIDADES = `${API_GATEWAY_BASE}/api/especialidades`;

let paginaAtual = 0;
const itensPorPagina = 5;
let totalPaginas = 1;
let especialidadeSelecionada = '';
let medicoEmEdicaoId = null;
let medicosCache = [];

// 🔒 Garante que o usuário esteja logado
document.addEventListener('DOMContentLoaded', () => {
  verificarLogin();

  carregarEspecialidadesForm();
  carregarEspecialidadesFiltro();
  carregarMedicos();

  document.getElementById('medicoForm').addEventListener('submit', salvarMedico);

  const cancelarBtn = document.getElementById('cancelarEdicao');
  if (cancelarBtn) cancelarBtn.addEventListener('click', cancelarEdicao);

  document.getElementById('prevPage').addEventListener('click', () => {
    if (paginaAtual > 0) {
      paginaAtual--;
      carregarMedicos();
    }
  });

  document.getElementById('nextPage').addEventListener('click', () => {
    if (paginaAtual < totalPaginas - 1) {
      paginaAtual++;
      carregarMedicos();
    }
  });

  document.getElementById('filtroEspecialidade').addEventListener('change', (e) => {
    especialidadeSelecionada = e.target.value;
    paginaAtual = 0;
    carregarMedicos();
  });
});

// ✅ Verifica token
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

// 🏥 Carrega especialidades (formulário)
async function carregarEspecialidadesForm() {
  try {
    const res = await fetch(API_ESPECIALIDADES, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);

    const data = await res.json();
    const select = document.getElementById('especialidades');
    if (!select) return;
    select.innerHTML = '';

    data.forEach(esp => {
      const option = document.createElement('option');
      option.value = esp.id;
      option.textContent = esp.nome;
      select.appendChild(option);
    });
  } catch (err) {
    console.error('❌ Erro ao carregar especialidades (form):', err);
  }
}

// 🎯 Carrega especialidades (filtro)
async function carregarEspecialidadesFiltro() {
  try {
    const res = await fetch(API_ESPECIALIDADES, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);

    const data = await res.json();
    const filtroSelect = document.getElementById('filtroEspecialidade');
    if (!filtroSelect) return;

    filtroSelect.innerHTML = '<option value="">Todas</option>';
    data.forEach(esp => {
      const option = document.createElement('option');
      option.value = esp.nome;
      option.textContent = esp.nome;
      filtroSelect.appendChild(option);
    });
  } catch (err) {
    console.error('❌ Erro ao carregar especialidades (filtro):', err);
  }
}

// 👨‍⚕️ Carrega médicos
async function carregarMedicos() {
  try {
    let url = especialidadeSelecionada
      ? `${API_MEDICOS}/especialidade/${encodeURIComponent(especialidadeSelecionada)}?page=${paginaAtual}&size=${itensPorPagina}`
      : `${API_MEDICOS}?page=${paginaAtual}&size=${itensPorPagina}`;

    const res = await fetch(url, {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);

    const data = await res.json();
    const medicos = Array.isArray(data.content) ? data.content : [];
    totalPaginas = data.totalPages || 1;

    medicosCache = medicos;
    renderizarMedicos(medicos);
  } catch (err) {
    console.error('❌ Erro ao carregar médicos:', err);
    document.getElementById('medicosList').innerHTML = '<p>Erro ao carregar médicos.</p>';
  }
}

// 🧩 Renderiza lista
function renderizarMedicos(medicos) {
  const container = document.getElementById('medicosList');
  const pageInfo = document.getElementById('pageInfo');
  const prevBtn = document.getElementById('prevPage');
  const nextBtn = document.getElementById('nextPage');

  container.innerHTML = '';

  if (!medicos || medicos.length === 0) {
    container.innerHTML = '<p>Nenhum médico encontrado.</p>';
  } else {
    medicos.forEach(medico => {
      const card = document.createElement('div');
      card.classList.add('medico-card');

      card.innerHTML = `
        <div class="medico-info">
          <p><strong>Nome:</strong> ${medico.nome}</p>
          <p><strong>CRM:</strong> ${medico.crm}</p>
          <p><strong>Endereço:</strong> ${medico.endereco}</p>
          <p><strong>Especialidades:</strong> ${medico.especialidades?.join(', ')}</p>
        </div>
        <div class="action-buttons">
          <button class="btn-secondary" onclick="iniciarEdicao(${medico.id})">Editar</button>
          <button class="btn-danger" onclick="excluirMedico(${medico.id})">Excluir</button>
        </div>
      `;
      container.appendChild(card);
    });
  }

  pageInfo.textContent = `Página ${paginaAtual + 1} de ${totalPaginas}`;
  prevBtn.disabled = paginaAtual === 0;
  nextBtn.disabled = paginaAtual >= totalPaginas - 1;
}

// 💾 Salvar médico
async function salvarMedico(event) {
  event.preventDefault();

  const nome = document.getElementById('nome').value.trim();
  const crm = document.getElementById('crm').value.trim();
  const endereco = document.getElementById('endereco').value.trim();
  const especialidades = Array.from(document.getElementById('especialidades').selectedOptions)
    .map(opt => parseInt(opt.value));

  const medico = { nome, crm, endereco, especialidades };
  const url = medicoEmEdicaoId ? `${API_MEDICOS}/${medicoEmEdicaoId}` : API_MEDICOS;
  const method = medicoEmEdicaoId ? 'PUT' : 'POST';

  try {
    const res = await fetch(url, {
      method,
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders(),
      body: JSON.stringify(medico)
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);
    alert(medicoEmEdicaoId ? 'Médico atualizado com sucesso!' : 'Médico cadastrado com sucesso!');
    cancelarEdicao();
    carregarMedicos();
  } catch (err) {
    console.error('❌ Erro ao salvar médico:', err);
    alert('Erro ao salvar médico.');
  }
}

// ❌ Excluir médico
async function excluirMedico(id) {
  if (!confirm('Deseja realmente excluir este médico?')) return;

  try {
    const res = await fetch(`${API_MEDICOS}/${id}`, {
      method: 'DELETE',
      mode: 'cors',
      credentials: 'include',
      headers: getAuthHeaders()
    });

    if (!res.ok) throw new Error(`Erro ${res.status}`);
    alert('Médico excluído com sucesso!');
    carregarMedicos();
  } catch (err) {
    console.error('❌ Erro ao excluir médico:', err);
    alert('Erro ao excluir médico.');
  }
}

function cancelarEdicao() {
  medicoEmEdicaoId = null;
  document.getElementById('formTitle').textContent = 'Novo Médico';
  document.getElementById('submitButton').textContent = 'Salvar Médico';
  document.getElementById('cancelarEdicao').classList.add('hidden');
  document.getElementById('medicoForm').reset();
}

// ✏️ Função para iniciar edição do médico
function iniciarEdicao(id) {
  const medico = medicosCache.find(m => m.id === id);
  if (!medico) {
    alert('Médico não encontrado!');
    return;
  }

  medicoEmEdicaoId = medico.id;
  document.getElementById('formTitle').textContent = 'Editar Médico';
  document.getElementById('submitButton').textContent = 'Atualizar Médico';
  document.getElementById('cancelarEdicao').classList.remove('hidden');

  // Preenche campos
  document.getElementById('nome').value = medico.nome || '';
  document.getElementById('crm').value = medico.crm || '';
  document.getElementById('endereco').value = medico.endereco || '';

  // Preenche especialidades selecionadas
  const select = document.getElementById('especialidades');
  if (!select.options.length) {
    // se o select ainda não foi carregado, busca as especialidades primeiro
    carregarEspecialidadesForm().then(() => selecionarEspecialidades(select, medico.especialidades));
  } else {
    selecionarEspecialidades(select, medico.especialidades);
  }
}

// 🧩 Seleciona as especialidades do médico
function selecionarEspecialidades(select, nomesEspecialidades) {
  const listaNomes = Array.isArray(nomesEspecialidades) ? nomesEspecialidades : [];
  Array.from(select.options).forEach(option => {
    option.selected = listaNomes.includes(option.textContent);
  });
}
