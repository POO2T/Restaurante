# 🔧 Guia de Diagnóstico - Relatório Financeiro Não Está Rodando

## 1️⃣ Verificar o Backend

### Backend está rodando?
```bash
# No terminal, verifique se há uma processo Java em 8080
# Windows:
netstat -ano | findstr :8080

# Se não houver, inicie o backend:
cd Back-End_Restaurante
mvn clean package
mvn spring-boot:run
# OU com perfil PostgreSQL:
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Teste a API diretamente
Abra no navegador ou Postman:
```
http://localhost:8080/api/pedidos
```

**Esperado:**
- Se retornar um JSON com array de pedidos → ✅ Backend OK
- Se mostrar "Cannot get" ou erro 404 → ❌ Endpoint não existe
- Se timeout → ❌ Backend não está rodando

---

## 2️⃣ Verificar o Frontend

### Console do Navegador (F12)
Pressione `F12` e vá para a aba **Console**

**Procure por mensagens como:**

```
✅ Dados carregados com sucesso: { totalPedidos: 5, ... }
```
Significa que funcionou!

```
❌ Erro de conexão: Verifique se o backend está rodando em http://localhost:8080
```
Significa que o backend não está respondendo.

```
❌ Endpoint não encontrado: /api/pedidos não existe
```
Significa que o endpoint não existe no backend.

---

## 3️⃣ Passo a Passo de Solução

### Passo 1: Inicie o Backend
```bash
cd Back-End_Restaurante
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```
Aguarde aparecer: `Started BackEndRestauranteApplication`

### Passo 2: Inicie o Frontend
```bash
cd Front-End_Restaurante
ng serve --open
```
Aguarde: `Application bundle generated successfully`

### Passo 3: Abra http://localhost:4200
Navegue até a página de Relatório

### Passo 4: Abra o Console (F12)
Verifique as mensagens de log

### Passo 5: Se ainda houver erro
- Cole a mensagem de erro aqui
- Verifique se há dados de pedidos no banco:
  ```sql
  SELECT COUNT(*) FROM pedidos;
  ```

---

## 4️⃣ Checklist Rápido

- [ ] Backend rodando em http://localhost:8080
- [ ] Frontend rodando em http://localhost:4200
- [ ] Banco de dados (PostgreSQL) conectado
- [ ] Há dados na tabela `pedidos`
- [ ] Sem firewalls bloqueando a comunicação
- [ ] Console do navegador não mostra erros
- [ ] Botão "Tentar Novamente" foi clicado após iniciar o backend

---

## 5️⃣ Logs Detalhados que Devem Aparecer

Se tudo estiver certo, veja no Console:

```
📦 Pedidos recebidos: Array(5)
  0: {id: 1, dataHora: "2025-11-30T10:30:00", status: "PENDENTE", itens: Array(2)}
  1: {id: 2, dataHora: "2025-11-30T11:15:00", status: "PENDENTE", itens: Array(1)}
  ...

✅ Dados carregados com sucesso: {
  totalPedidos: 5,
  receita: 1500.00,
  lucro: 1050.00,
  itens: 7
}
```

---

## 6️⃣ Mensagens de Erro Comuns

| Mensagem | Causa | Solução |
|----------|-------|--------|
| "Erro de conexão" | Backend não respondendo | Inicie o backend em 8080 |
| "Endpoint não encontrado" | `/api/pedidos` não existe | Verifique se PedidoController existe |
| "Acesso negado" | Sem autenticação | Verifique JWT token |
| "Nenhum pedido encontrado" | Banco vazio | Crie pedidos de teste |

---

## 7️⃣ Teste Rápido da API (Postman/curl)

### Com curl:
```bash
curl -X GET http://localhost:8080/api/pedidos
```

### Com Postman:
1. Crie um GET request para `http://localhost:8080/api/pedidos`
2. Clique em "Send"
3. Se retornar JSON com pedidos → Backend OK ✅

---

## ⚠️ Se Ainda Não Funcionar

1. **Tire um screenshot do erro**
2. **Abra DevTools (F12) → Console**
3. **Cole aqui a mensagem de erro completa**
4. **Cole o resultado de:** `http://localhost:8080/api/pedidos` (abra no navegador)

Com essas informações poderei diagnosticar o problema específico!
