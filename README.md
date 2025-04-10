# 💼 Worker Contracts Java

Hands-on Java project using OOP to manage worker data, contracts, and monthly earnings.

Sistema de gerenciamento de contratos de trabalho para um funcionário de um departamento. É possível cadastrar contratos por hora e calcular a renda total do trabalhador em um determinado mês.

## 🛠️ Funcionalidades

- Cadastro de funcionário com nome, nível e salário base.
- Associação do funcionário a um departamento.
- Cadastro de múltiplos contratos de trabalho (data, valor por hora, duração).
- Cálculo da renda do trabalhador em um mês específico (salário base + contratos).

## 📦 Estrutura do Projeto

```
WorkerContracts-java/
├── src/
│   ├── application/ → Classe principal com o fluxo do programa (Program.java)
│   └── entities/    → Classes de modelo: Worker, Department, HourContract, WorkerLevel
└── README.md
```

## 🚀 Como executar

1. Clone o repositório:

```bash
git clone https://github.com/yannpeclat/WorkerContracts-java.git
```

2. Compile e execute o arquivo `Program.java` dentro da pasta `src/application`

## 📌 Futuras implementações

- Validação automática de datas e campos obrigatórios
- Interface gráfica (GUI) com JavaFX ou Swing
- Persistência de dados com arquivos ou banco de dados

## 🌐 English Summary

This is a Java console application to manage a worker's hourly contracts. It allows registering the worker's details, multiple hourly contracts, and calculating total income in a given month.

## 💡 Sobre mim

Desenvolvido por [Yann Peclat](https://github.com/yannpeclat), estudante de Java focado em back-end, arquitetura de software e boas práticas de desenvolvimento. Esse projeto faz parte da minha jornada de aprendizado. ⭐
