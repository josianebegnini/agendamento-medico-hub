document.getElementById("registerForm").addEventListener("submit", async function (event) {
  event.preventDefault();

  const username = document.getElementById("username").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();

  const errorMessage = document.getElementById("errorMessage");
  const successMessage = document.getElementById("successMessage");

  errorMessage.textContent = "";
  successMessage.textContent = "";

  try {
    const response = await fetch("http://localhost:8085/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, password }),
    });

    if (response.ok) {
      successMessage.textContent = "Usuário cadastrado com sucesso!";
      document.getElementById("registerForm").reset();

      // Redireciona após 2 segundos
      setTimeout(() => {
        window.location.href = "login.html";
      }, 2000);
    } else {
      const data = await response.json();
      errorMessage.textContent = data.message || "Erro ao cadastrar usuário.";
    }
  } catch (err) {
    errorMessage.textContent = "Erro de conexão com o servidor.";
  }
});
