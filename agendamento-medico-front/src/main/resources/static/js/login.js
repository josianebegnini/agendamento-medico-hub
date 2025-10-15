document.getElementById('loginForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value.trim();
  const errorMessage = document.getElementById('errorMessage');
  errorMessage.textContent = '';

  try {
    const response = await fetch('http://localhost:8085/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });

    const data = await response.json();

    if (response.ok) {
      localStorage.setItem('token', data.token);

      window.location.href = '/';
    } else {
      errorMessage.textContent = data.message || 'Usuário ou senha inválidos.';
    }
  } catch (error) {
    console.error('Erro de conexão:', error);
    errorMessage.textContent = 'Erro ao conectar ao servidor.';
  }
});

document.getElementById('btnCadastrar').addEventListener('click', () => {
  window.location.href = 'register.html';
});
