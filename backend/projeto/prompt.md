from pathlib import Path

content = r"""# Configuração e execução da API Spring Boot — Java 21

> **Objetivo:** orientar a configuração do ambiente, a verificação do Spring Security e a inicialização de uma API Spring Boot existente no Windows.
>
> **Importante:** este documento é um guia-base. As versões do projeto, endpoints, dependências, variáveis de ambiente e configurações reais devem ser confirmados nos arquivos do repositório antes de executar comandos ou aplicar mudanças.

## Sumário

- [1. Pré-requisitos](#1-pré-requisitos)
- [2. Configurar e validar o Java 21](#2-configurar-e-validar-o-java-21)
- [3. Identificar o sistema de build](#3-identificar-o-sistema-de-build)
- [4. Verificar dependências e compilar](#4-verificar-dependências-e-compilar)
- [5. Revisar o Spring Security](#5-revisar-o-spring-security)
- [6. Configuração do banco de dados](#6-configuração-do-banco-de-dados)
- [7. Variáveis de ambiente](#7-variáveis-de-ambiente)
- [8. Iniciar a aplicação](#8-iniciar-a-aplicação)
- [9. Testes e verificações](#9-testes-e-verificações)
- [10. Diagnóstico de problemas](#10-diagnóstico-de-problemas)
- [11. Checklist final](#11-checklist-final)

## 1. Pré-requisitos

Antes de iniciar, confirme quais ferramentas e tecnologias o projeto realmente utiliza:

- JDK 21 instalado.
- Maven ou Gradle, conforme o repositório.
- IDE configurada para usar o JDK correto, se for executar por ela.
- Banco de dados disponível, **somente se a aplicação precisar dele**.
- Variáveis de ambiente necessárias definidas.
- Acesso às dependências utilizadas pelo projeto.

Não presuma que o projeto utiliza JWT, OAuth2, autenticação HTTP Basic, banco de dados específico ou uma porta determinada. Confirme essas informações nos arquivos do repositório.

## 2. Configurar e validar o Java 21

O JDK informado para este ambiente está localizado em:

```text
C:\Users\grasi\.jdks\jbr-21.0.11
```

O caminho é de um JetBrains Runtime (JBR) baseado no OpenJDK. Confirme que a instalação contém as ferramentas necessárias, incluindo `java.exe` e `javac.exe`.

### 2.1. Configuração temporária no PowerShell

Os comandos abaixo alteram as variáveis **somente na sessão atual** do PowerShell. Eles não modificam permanentemente as variáveis globais do Windows.

```powershell
$env:JAVA_HOME = "C:\Users\grasi\.jdks\jbr-21.0.11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

java -version
javac -version
```

O que cada comando faz:

- `JAVA_HOME`: informa o caminho do JDK para ferramentas que consultam essa variável.
- `PATH`: permite localizar os executáveis Java pelo terminal.
- `java -version`: mostra a versão do runtime Java selecionado.
- `javac -version`: confirma a disponibilidade do compilador Java.

Confira também se os executáveis existem:

```powershell
Test-Path "$env:JAVA_HOME\bin\java.exe"
Test-Path "$env:JAVA_HOME\bin\javac.exe"
```

O resultado esperado para ambos é `True`. Se algum resultado for `False`, confira o caminho informado e a instalação do JDK.

### 2.2. Conferir o Java utilizado pelo build

O terminal, o Maven/Gradle e a IDE podem usar configurações diferentes. Por isso, valide também o Java utilizado pelo sistema de build.

Para Maven Wrapper no Windows:

```powershell
.\mvnw.cmd -version
```

Para Gradle Wrapper no Windows:

```powershell
.\gradlew.bat --version
```

Execute o comando correspondente ao wrapper existente no projeto. Se o resultado indicar uma versão diferente de Java 21, revise `JAVA_HOME`, `PATH`, as configurações da IDE e qualquer configuração explícita de toolchain.

## 3. Identificar o sistema de build

Na raiz do repositório, procure por:

| Arquivo | Indicação |
|---|---|
| `pom.xml` | Projeto Maven |
| `mvnw.cmd` | Maven Wrapper para Windows |
| `build.gradle` ou `build.gradle.kts` | Projeto Gradle |
| `gradlew.bat` | Gradle Wrapper para Windows |

Leia o arquivo de build antes de alterar qualquer versão ou dependência. Confirme a versão do Java, a versão do Spring Boot, os plugins e as bibliotecas declaradas.

Prefira o wrapper do repositório, quando disponível, pois ele ajuda a utilizar a versão de build esperada pelo projeto.

## 4. Verificar dependências e compilar

Confira no arquivo de build quais dependências estão realmente declaradas. Conforme o projeto, podem existir dependências para:

- Spring Boot e Spring Web;
- Spring Security;
- Spring Data JPA;
- driver do banco de dados;
- Flyway ou Liquibase;
- Bean Validation;
- Spring Boot Actuator;
- testes automatizados.

Essa lista é apenas uma referência de itens possíveis, não uma afirmação de que todos estejam presentes.

### 4.1. Maven

Se o projeto possuir `mvnw.cmd`, execute na raiz:

```powershell
.\mvnw.cmd clean test
```

Esse comando limpa os artefatos de compilação anteriores, compila o projeto e executa os testes configurados. Ele pode falhar se houver testes quebrados, dependências indisponíveis ou configurações de ambiente ausentes.

Se não houver Maven Wrapper, confirme se o Maven está instalado antes de usar `mvn`.

### 4.2. Gradle

Se o projeto possuir `gradlew.bat`, utilize os comandos compatíveis com sua configuração, por exemplo:

```powershell
.\gradlew.bat test
```

Não misture comandos Maven e Gradle: utilize o sistema de build identificado no repositório.

## 5. Revisar o Spring Security

Antes de alterar a segurança, inspecione as classes de configuração e a implementação de autenticação existentes. Procure, quando aplicável, por:

- `SecurityFilterChain`;
- regras de autorização e `requestMatchers`;
- `UserDetailsService` ou implementação equivalente;
- `PasswordEncoder`;
- filtros ou serviços de autenticação;
- configuração de sessões ou tokens;
- regras de CORS e CSRF.

### 5.1. O que documentar

Registre, com base no código real:

1. Onde está a configuração de segurança.
2. Quais rotas são públicas.
3. Quais rotas exigem autenticação.
4. Como os usuários são autenticados.
5. Como são definidas as permissões.
6. Como são tratadas as senhas, se houver autenticação por senha.
7. Como são tratados CORS e CSRF, quando aplicável.

Não invente endpoints ou classes para preencher a documentação. Se não houver configuração suficiente, registre a pendência e explique quais decisões precisam ser tomadas antes da implementação.

### 5.2. Cuidados importantes

- Não libere todas as rotas com `permitAll()` apenas para fazer a API iniciar.
- Não desabilite CSRF sem avaliar o mecanismo de autenticação e o tipo de cliente.
- Não configure CORS irrestrito por padrão.
- Não armazene senhas em texto puro.
- Não crie uma implementação JWT fictícia ou incompleta.
- Não remova o Spring Security como solução para erros de inicialização.
- Confira a compatibilidade das APIs utilizadas com as versões reais do Spring Boot e do Spring Security.

A aplicação iniciar sem erros não comprova, por si só, que as regras de segurança estão corretas.

## 6. Configuração do banco de dados

Se a aplicação utilizar banco de dados, inspecione `application.properties`, `application.yml` e suas variantes por ambiente. Confirme:

- tipo de banco e driver;
- URL de conexão;
- usuário e senha fornecidos por configuração segura;
- variáveis de ambiente utilizadas;
- configuração do JPA/Hibernate;
- migrações Flyway ou Liquibase, se existirem.

Não invente valores de conexão nem inclua credenciais reais neste documento.

Não recomende apagar tabelas ou recriar o banco para corrigir problemas de inicialização. Evite estratégias que possam sobrescrever dados existentes. Se houver migrações, siga o procedimento documentado pelo projeto.

Se não for possível identificar uma variável obrigatória, registre-a como pendência em vez de presumir seu nome ou valor.

## 7. Variáveis de ambiente

Liste somente as variáveis que o código ou os arquivos de configuração realmente utilizam. Para cada variável, documente:

- nome exato;
- finalidade;
- se é obrigatória ou opcional, quando isso puder ser confirmado;
- como obter ou definir o valor;
- se existe valor padrão configurado.

Exemplo genérico de variável temporária no PowerShell:

```powershell
$env:NOME_DA_VARIAVEL = "valor_de_exemplo"
```

`NOME_DA_VARIAVEL` e `valor_de_exemplo` são placeholders, não nomes ou credenciais confirmados do projeto.

Nunca registre senhas, tokens ou chaves secretas reais em documentação versionada. Utilize um arquivo de exemplo sem segredos, caso isso seja apropriado à estrutura do repositório.

## 8. Iniciar a aplicação

Execute o comando correspondente ao sistema de build identificado e à configuração do projeto.

### 8.1. Maven

Se houver Maven Wrapper e o plugin Spring Boot estiver configurado:

```powershell
.\mvnw.cmd spring-boot:run
```

### 8.2. Gradle

Se houver Gradle Wrapper e a tarefa Spring Boot estiver disponível:

```powershell
.\gradlew.bat bootRun
```

### 8.3. Pela IDE

Se for possível identificar a classe principal, execute-a pela IDE usando o JDK 21. Confira a configuração de execução da IDE, pois ela pode não utilizar o mesmo Java do terminal.

### 8.4. Confirmar a inicialização

Leia os logs e confirme que a aplicação concluiu a inicialização sem exceções. Verifique a porta nas configurações do projeto ou nos próprios logs; não presuma que seja `8080`.

Se a inicialização falhar, guarde a mensagem de erro completa e identifique se a causa está relacionada ao Java, build, dependências, variáveis de ambiente, banco de dados, migrações ou segurança.

## 9. Testes e verificações

Execute os testes disponíveis no projeto e registre os resultados reais. Conforme a implementação existente, verifique:

- compilação e testes automatizados;
- inicialização da aplicação;
- disponibilidade dos endpoints reais;
- acesso às rotas públicas;
- bloqueio de rotas protegidas para usuários não autenticados;
- permissões dos usuários autenticados;
- conexão com o banco de dados;
- comportamento das migrações.

Não invente resultados. Se não houver testes de segurança, registre essa limitação. Se algum teste não puder ser executado por falta de configuração, documente o motivo.

## 10. Diagnóstico de problemas

Use a tabela como referência. Confirme a causa antes de aplicar uma solução.

| Problema | Como verificar | Próxima ação |
|---|---|---|
| Java incorreto | Execute `java -version` e o comando de versão do wrapper | Confira `JAVA_HOME`, `PATH` e a configuração da IDE |
| Compilação falha | Leia o primeiro erro relevante do build | Verifique compatibilidade, código e dependências |
| Dependência não resolvida | Examine a mensagem do Maven/Gradle | Confira versão, repositório e conectividade |
| Porta ocupada | Leia o erro de bind nos logs | Identifique o processo ou configure uma porta livre |
| Banco indisponível | Examine a causa da exceção de conexão | Confira serviço, URL, credenciais e rede |
| Variável ausente | Procure a mensagem e a configuração que a referencia | Defina a variável correta sem inventar seu nome |
| Migração falha | Leia o erro da ferramenta de migração | Revise o arquivo de migração e o estado do banco |
| Acesso negado | Confira status HTTP e regras de segurança | Revise autenticação, autorização e configuração da rota |
| Aplicação inicia, mas rota falha | Confira logs, método HTTP e mapeamento real | Teste o endpoint existente e suas dependências |

## 11. Checklist final

Marque cada item somente depois de validá-lo:

- [ ] JDK 21 localizado e validado.
- [ ] Maven ou Gradle identificado e funcionando.
- [ ] Versões do projeto verificadas.
- [ ] Dependências resolvidas.
- [ ] Variáveis de ambiente necessárias configuradas.
- [ ] Banco de dados disponível, se aplicável.
- [ ] Migrações verificadas, se aplicável.
- [ ] Configuração do Spring Security revisada.
- [ ] Rotas públicas e protegidas identificadas.
- [ ] Testes executados ou limitações documentadas.
- [ ] Aplicação iniciada e resultado confirmado.
- [ ] Endpoints relevantes testados.

## Registro específico do repositório

Antes de considerar este guia definitivo, preencha com informações verificadas:

- **Nome do projeto:** a confirmar no repositório.
- **Versão do Java configurada:** a verificar.
- **Versão do Spring Boot:** a verificar no arquivo de build.
- **Versão do Spring Security:** a verificar nas dependências resolvidas.
- **Sistema de build:** a verificar.
- **Banco de dados:** a verificar.
- **Porta da aplicação:** a verificar.
- **Classe principal:** a localizar.
- **Endpoints públicos e protegidos:** a verificar na configuração de segurança.
- **Resultado de compilação e testes:** a executar e registrar.
- **Resultado da inicialização:** a executar e registrar.

> **Princípio:** analisar primeiro, configurar com base em evidências e validar por último. Não declare que a API está funcional sem confirmar o resultado da execução.
"""

path = Path("/mnt/data/CONFIGURACAO_E_EXECUCAO.md")
path.write_text(content, encoding="utf-8")
print(f"Arquivo criado: {path}")
