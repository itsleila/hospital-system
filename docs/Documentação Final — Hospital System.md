# Hospital System — Documentação Final do Projeto

## 1. Introdução

O **Hospital System** foi desenvolvido com o objetivo de aplicar conceitos de arquitetura de software, Domain-Driven Design, microsserviços, persistência de dados, comunicação síncrona e assíncrona, conteinerização, orquestração, observabilidade e integração contínua.

A área principal do sistema é a **gestão hospitalar**, responsável pelo gerenciamento de pacientes, médicos e consultas. Ao longo da evolução do projeto, a aplicação passou de uma solução com armazenamento temporário e maior acoplamento entre os componentes para uma arquitetura distribuída, com persistência em PostgreSQL, descoberta de serviços, comunicação orientada a eventos utilizando RabbitMQ e infraestrutura preparada para execução por meio de Docker e Kubernetes.

A arquitetura final é composta principalmente pelos seguintes componentes:

```text
hospital-system-web
        ↓
    API Gateway
        ↓
Hospital System API
   │            │
   │            └── RabbitMQ ──→ Notification Service
   │                                 │
   ↓                                 ↓
PostgreSQL                       PostgreSQL

        Discovery Server / Eureka
```

---

# 2. Modelagem do domínio

O domínio principal da aplicação é a **gestão hospitalar**, pois representa a base do negócio e concentra as funcionalidades necessárias para o gerenciamento dos atendimentos realizados no sistema.

Com o objetivo de separar responsabilidades e regras de negócio, foram identificados três subdomínios principais:

- Patients;
- Doctors;
- Appointments.

O subdomínio **Appointments** foi classificado como **Core Domain**, pois concentra as principais regras do negócio e representa a principal funcionalidade da aplicação. Ele é responsável pelo gerenciamento das consultas, relacionando pacientes, médicos, datas, horários e estados do atendimento.

Os subdomínios **Patients** e **Doctors** foram classificados como **Supporting Subdomains**, pois fornecem as informações necessárias para o funcionamento do domínio principal, mas não representam o principal diferencial da aplicação.

A divisão adotada pode ser representada como:

```text
Gestão Hospitalar
│
├── Patients
│   └── Supporting Subdomain
│
├── Doctors
│   └── Supporting Subdomain
│
└── Appointments
    └── Core Domain
```

---

# 3. Bounded Contexts

Com o objetivo de delimitar as responsabilidades da aplicação, foram definidos os seguintes **Bounded Contexts**:

```text
Patients Context
Doctors Context
Appointments Context
```

Cada contexto possui seus próprios modelos, regras e comportamentos.

O contexto de **Appointments** concentra as regras relacionadas às consultas, enquanto Patients e Doctors são responsáveis pelo gerenciamento das informações necessárias para que essas consultas possam ser realizadas.

Essa divisão contribui para:

- redução do acoplamento;
- maior organização das regras de negócio;
- separação de responsabilidades;
- facilidade de manutenção;
- possibilidade de evolução independente dos módulos.

---

# 4. Camada de persistência

Inicialmente, os dados utilizados pela aplicação eram armazenados temporariamente em listas dentro das classes de serviço.

Essa implementação apresentava uma limitação importante: todas as informações eram perdidas sempre que a aplicação era reiniciada.

Com a evolução do projeto, foi introduzida uma camada de persistência utilizando:

```text
Spring Data JPA
Hibernate
PostgreSQL
Hibernate Envers
```

A arquitetura passou a seguir aproximadamente o fluxo:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Spring Data JPA
    ↓
Hibernate
    ↓
PostgreSQL
```

Os repositórios Spring Data são responsáveis pelas operações de armazenamento, recuperação e atualização das entidades.

Dessa forma, o sistema deixou de utilizar armazenamento temporário e passou a trabalhar com dados persistentes.

---

# 5. Persistência e histórico

Além da persistência convencional, o projeto utiliza **Hibernate Envers** para permitir a auditoria de alterações realizadas nas entidades.

Com isso, alterações importantes podem possuir um histórico de revisões, permitindo identificar diferentes estados de um registro durante seu ciclo de vida.

No caso das consultas, o cancelamento também passou a representar uma alteração de estado, em vez da exclusão física do registro.

Por exemplo:

```text
SCHEDULED
    ↓
CANCELLED
```

Assim, a consulta permanece registrada no banco, preservando o histórico das operações realizadas.

---

# 6. Arquitetura de microsserviços

Durante a evolução do projeto foi identificado que o gerenciamento de notificações poderia possuir responsabilidades próprias e ser executado independentemente da aplicação hospitalar principal.

Foi então criado o:

```text
hospital-notification-service
```

como uma aplicação Spring Boot independente.

A arquitetura passou a possuir:

```text
Hospital System API

Hospital Notification Service

API Gateway

Discovery Server

RabbitMQ
```

Cada aplicação possui seu próprio ciclo de vida e pode ser implantada de maneira independente.

---

# 7. Microserviço de notificações

O **Notification Service** é responsável pelo gerenciamento das notificações relacionadas às consultas médicas.

Uma notificação possui informações como:

```text
appointmentId
patientId
patientName
patientPhone
doctorName
appointmentDateTime
type
status
createdAt
sentAt
eventId
```

Os principais tipos de notificação são:

```text
APPOINTMENT_CREATED
APPOINTMENT_UPDATED
APPOINTMENT_CANCELLED
APPOINTMENT_REMINDER
```

Enquanto os principais estados são:

```text
PENDING
SENT
FAILED
CANCELLED
```

Cada acontecimento relevante relacionado a uma consulta pode gerar uma nova notificação.

As notificações anteriores não são sobrescritas, permitindo manter um histórico das alterações ocorridas durante o ciclo de vida de uma consulta.

Exemplo:

```text
Consulta criada
      ↓
APPOINTMENT_CREATED

Consulta alterada
      ↓
APPOINTMENT_UPDATED

Consulta cancelada
      ↓
APPOINTMENT_CANCELLED
```

---

# 8. Service Discovery com Eureka

Como os microsserviços podem possuir endereços diferentes durante sua execução, foi introduzido um **Discovery Server utilizando Netflix Eureka**.

Os serviços registram suas instâncias no Eureka:

```text
Hospital System API ──┐
                      │
Notification Service ─┼──→ Eureka Server
                      │
API Gateway ──────────┘
```

Dessa forma, os componentes podem localizar instâncias disponíveis sem depender diretamente de endereços físicos fixos.

---

# 9. API Gateway

Foi introduzido um **API Gateway** como ponto central de entrada das requisições realizadas pelo frontend.

O fluxo principal das requisições HTTP é:

```text
Frontend
   ↓
API Gateway
   ↓
Microserviço correspondente
```

O Gateway utiliza integração com Eureka para localizar os serviços disponíveis.

Exemplos de roteamento:

```text
/appointments/**
        ↓
Hospital System API


/notifications/**
        ↓
Notification Service
```

Dessa forma, o frontend não precisa conhecer diretamente o endereço físico de cada microserviço.

---

# 10. Evolução da comunicação entre os microsserviços

Inicialmente, a Hospital System API utilizava comunicação síncrona com o Notification Service utilizando **OpenFeign**.

Nesse modelo:

```text
AppointmentService
      ↓
OpenFeign
      ↓
Notification Service
```

A Hospital System API precisava realizar uma chamada HTTP para criar uma notificação.

Embora funcional, essa abordagem gerava maior dependência entre os serviços.

Se o Notification Service estivesse indisponível no momento da chamada, a operação poderia ser afetada.

Com a evolução do projeto, a criação das notificações passou a utilizar uma **arquitetura orientada a eventos utilizando RabbitMQ**.

---

# 11. Arquitetura orientada a eventos

Uma arquitetura orientada a eventos permite que os microsserviços se comuniquem de maneira assíncrona.

Nesse modelo, um serviço publica um evento sem precisar aguardar imediatamente o processamento realizado por outro serviço.

No projeto, o fluxo passou a ser:

```text
AppointmentService
       ↓
AppointmentEvent
       ↓
RabbitMQ
       ↓
Notification Service
       ↓
Notification
```

A Hospital System API atua como **produtora**, enquanto o Notification Service atua como **consumidor**.

---

# 12. RabbitMQ

O RabbitMQ foi escolhido como **Message Broker** responsável por receber, armazenar e encaminhar eventos entre os microsserviços.

Foi criado o exchange:

```text
hospital.appointments.exchange
```

com as seguintes routing keys:

```text
appointment.created
appointment.updated
appointment.cancelled
```

O Notification Service utiliza a fila:

```text
notification.appointments.queue
```

com binding:

```text
appointment.*
```

O fluxo pode ser representado como:

```text
Hospital System API
        ↓
hospital.appointments.exchange
        ↓
appointment.created
appointment.updated
appointment.cancelled
        ↓
notification.appointments.queue
        ↓
Notification Service
```

---

# 13. Publicação dos eventos

Após a execução das operações sobre uma consulta, a Hospital System API cria um `AppointmentEvent`.

O evento contém informações suficientes para que o Notification Service processe a mensagem sem precisar acessar diretamente o banco da aplicação principal.

Exemplo conceitual:

```text
AppointmentEvent
│
├── eventId
├── appointmentId
├── patientId
├── patientName
├── patientPhone
├── doctorName
├── appointmentDateTime
├── eventType
└── occurredAt
```

Os eventos são publicados após a confirmação da transação da consulta, evitando o envio de eventos relacionados a operações que tenham sofrido rollback.

---

# 14. Consumo e idempotência

O Notification Service utiliza um `@RabbitListener` para consumir as mensagens disponíveis na fila.

Após receber o evento:

```text
RabbitMQ
   ↓
AppointmentEventListener
   ↓
NotificationService
   ↓
PostgreSQL
```

Como sistemas de mensageria podem realizar uma nova entrega da mesma mensagem, foi implementado um mecanismo de **idempotência**.

Cada evento possui:

```text
eventId
```

e esse identificador é armazenado na notificação com uma restrição de unicidade.

Antes de salvar uma nova notificação, o sistema verifica se aquele `eventId` já foi processado.

Assim:

```text
Evento ABC recebido
      ↓
não existe
      ↓
Notification criada


Evento ABC recebido novamente
      ↓
eventId já existe
      ↓
nenhuma duplicação
```

---

# 15. Benefícios da comunicação assíncrona

A utilização do RabbitMQ trouxe maior desacoplamento entre os microsserviços.

Por exemplo, se o Notification Service estiver temporariamente indisponível:

```text
Hospital System API
        ↓
Evento
        ↓
RabbitMQ
        ↓
Mensagem permanece na fila
```

Quando o Notification Service voltar a ficar disponível:

```text
Fila
 ↓
Notification Service
 ↓
processamento
```

Dessa forma, a Hospital System API pode continuar processando consultas sem depender da disponibilidade imediata do serviço de notificações.

Entre as vantagens estão:

- menor acoplamento;
- processamento assíncrono;
- maior resiliência;
- possibilidade de processamento posterior;
- facilidade de escalabilidade;
- retenção das mensagens na fila enquanto não forem consumidas.

Entretanto, uma arquitetura orientada a eventos também introduz maior complexidade e exige cuidados relacionados a duplicação, ordenação, retries, persistência e observabilidade.

---

# 16. Endpoints de notificações

Com a migração para arquitetura orientada a eventos, a criação normal das notificações deixou de depender de um endpoint `POST`.

As notificações são criadas automaticamente a partir dos eventos consumidos do RabbitMQ.

Os endpoints REST permanecem disponíveis principalmente para consulta e operações administrativas.

| Método | Endpoint                         | Descrição                                            |
| ------ | -------------------------------- | ---------------------------------------------------- |
| GET    | `/notifications`                 | Lista todas as notificações                          |
| GET    | `/notifications/{id}`            | Busca uma notificação por ID                         |
| GET    | `/notifications/status/{status}` | Filtra notificações por status                       |
| POST   | `/notifications/{id}/retry`      | Solicita nova tentativa de uma notificação com falha |
| PATCH  | `/notifications/{id}/cancel`     | Cancela uma notificação válida                       |

Assim, existem dois estilos de comunicação no sistema:

```text
RabbitMQ
→ criação assíncrona de notificações

REST
→ consultas e comandos que exigem resposta imediata
```

---

# 17. Frontend

O frontend foi desenvolvido utilizando React e continua utilizando HTTP para comunicação com o sistema.

Ele não precisa conhecer RabbitMQ nem publicar eventos diretamente.

O fluxo é:

```text
React
 ↓
API Gateway
 ↓
APIs
```

A comunicação assíncrona permanece uma responsabilidade dos serviços backend.

Esse isolamento evita introduzir detalhes de infraestrutura de mensageria no frontend.

---

# 18. Conteinerização com Docker

Cada aplicação possui seu próprio `Dockerfile`, possibilitando que seja executada em um ambiente isolado e reproduzível.

Foram considerados os seguintes componentes:

```text
hospital-system-web
hospital-system-api
hospital-notification-service
api-gateway
discovery-server
```

Além deles, imagens oficiais são utilizadas para:

```text
PostgreSQL
RabbitMQ
```

O ambiente pode ser inicializado por meio de **Docker Compose**, permitindo levantar toda a infraestrutura necessária de maneira integrada.

---

# 19. Bancos de dados

A arquitetura mantém bancos independentes para os serviços.

```text
Hospital System API
        ↓
hospital-db


Notification Service
        ↓
notification-db
```

Essa abordagem evita que um microserviço acesse diretamente as tabelas pertencentes a outro contexto.

Cada serviço é responsável pelo gerenciamento de seus próprios dados.

---

# 20. Kubernetes

O Kubernetes foi introduzido para representar a etapa de orquestração dos contêineres.

Foram definidos manifests para os diferentes componentes da aplicação.

Exemplo da estrutura:

```text
kubernetes/
│
├── namespace.yaml
├── configmap.yaml
├── hospital-api.yaml
├── notification-service.yaml
├── gateway.yaml
├── discovery.yaml
├── frontend.yaml
├── hospital-db.yaml
├── notification-db.yaml
└── rabbitmq.yaml
```

Os serviços stateless utilizam principalmente:

```text
Deployment
+
Service
```

enquanto componentes que precisam manter estado, como PostgreSQL e RabbitMQ, utilizam:

```text
StatefulSet
+
Service
+
PersistentVolumeClaim
```

---

# 21. Persistência no Kubernetes

Os bancos e o RabbitMQ utilizam volumes persistentes.

Assim, se um Pod for excluído:

```text
Pod
 ↓
removido
 ↓
StatefulSet cria outro Pod
 ↓
PersistentVolume é montado novamente
 ↓
dados continuam disponíveis
```

Isso evita que informações sejam perdidas simplesmente pela reinicialização ou recriação de um contêiner.

---

# 22. ConfigMap e Secrets

As configurações da aplicação foram externalizadas.

Informações não sensíveis podem ser armazenadas em um `ConfigMap`.

Exemplos:

```text
EUREKA_URL
RABBITMQ_HOST
RABBITMQ_PORT
HOSPITAL_DB_URL
NOTIFICATION_DB_URL
OTEL_ENDPOINT
```

Credenciais são armazenadas utilizando `Secret`.

Exemplos:

```text
HOSPITAL_DB_USERNAME
HOSPITAL_DB_PASSWORD

NOTIFICATION_DB_USERNAME
NOTIFICATION_DB_PASSWORD

RABBITMQ_USER
RABBITMQ_PASSWORD
```

Essa abordagem evita inserir diretamente credenciais sensíveis no código-fonte.

---

# 23. Monitoramento e observabilidade

O projeto foi preparado para disponibilizar informações operacionais utilizando **Spring Boot Actuator e Micrometer**.

Entre as configurações utilizadas estão:

```properties
management.endpoints.web.exposure.include=health,info,prometheus

management.endpoint.health.probes.enabled=true

management.tracing.sampling.probability=1.0
```

O Actuator disponibiliza informações relacionadas à saúde da aplicação e fornece uma base para integração com ferramentas externas de monitoramento.

---

# 24. Métricas com Prometheus

Com o Micrometer Prometheus, os serviços disponibilizam métricas em:

```text
/actuator/prometheus
```

Entre as métricas que podem ser monitoradas estão:

- utilização de memória;
- funcionamento da JVM;
- requisições HTTP;
- tempos de resposta;
- quantidade de erros;
- estado das aplicações.

Esses dados podem ser coletados pelo Prometheus e posteriormente visualizados em dashboards.

---

# 25. Tracing distribuído

Para permitir o rastreamento de uma operação entre diferentes serviços, foi adicionada integração com **OpenTelemetry**.

A configuração utiliza um endpoint OTLP:

```properties
management.opentelemetry.tracing.export.otlp.endpoint=${OTEL_ENDPOINT:http://localhost:4318/v1/traces}
```

No ambiente Kubernetes, esse endpoint pode apontar para um serviço de tracing, como Grafana Tempo.

O objetivo é possibilitar a visualização de fluxos como:

```text
Frontend
   ↓
API Gateway
   ↓
Hospital API
   ↓
RabbitMQ
   ↓
Notification Service
```

---

# 26. Observabilidade do RabbitMQ

Também foi habilitada a observabilidade da comunicação assíncrona.

Na Hospital System API:

```properties
spring.rabbitmq.template.observation-enabled=true
```

No Notification Service:

```properties
spring.rabbitmq.listener.simple.observation-enabled=true
```

Isso permite que operações de publicação e consumo de mensagens também façam parte das informações de observabilidade geradas pelos serviços.

---

# 27. Agregação de logs

A arquitetura de observabilidade prevê a centralização dos logs dos microsserviços.

Uma possível estrutura é:

```text
Pods
 ↓
stdout
 ↓
Grafana Alloy
 ↓
Loki
 ↓
Grafana
```

A pilha completa de observabilidade pode ser representada como:

```text
Prometheus → métricas
Loki       → logs
Tempo      → traces
Grafana    → visualização
```

A centralização facilita a identificação de problemas que envolvam mais de um microserviço.

---

# 28. Testes automatizados

Foram implementados testes automatizados para validar diferentes partes da aplicação.

Entre as ferramentas utilizadas estão:

```text
JUnit
Mockito
Spring Boot Test
Testcontainers
```

Testes unitários utilizam mocks para evitar dependências desnecessárias de infraestrutura.

Por exemplo, no `NotificationServiceTest`, o `NotificationRepository` pode ser substituído por um mock.

Foram testados cenários relacionados a:

- criação de notificações;
- definição do estado inicial;
- busca por ID;
- busca por status;
- retry de notificações com falha;
- cancelamento;
- tratamento de registros inexistentes;
- idempotência por `eventId`.

A camada de persistência também possui testes utilizando PostgreSQL por meio do Testcontainers.

---

# 29. Versionamento com Git e GitHub

O código-fonte é versionado utilizando Git e armazenado no GitHub.

As alterações realizadas durante o projeto foram registradas por meio de commits, permitindo acompanhar a evolução da aplicação.

Exemplos:

```text
feat: implement RabbitMQ event communication

feat: add Kubernetes deployment

test: add notification service unit tests

ci: configure GitHub Actions pipeline

fix: correct discovery server path in CI

docs: update project documentation
```

---

# 30. Integração Contínua com GitHub Actions

Foi criado o workflow:

```text
.github/workflows/ci.yml
```

responsável pela **Integração Contínua (CI)**.

A cada push ou Pull Request configurado, o GitHub Actions executa automaticamente a validação dos projetos.

Para os serviços Java, o fluxo executa:

```text
Checkout
   ↓
Java 21
   ↓
Maven
   ↓
mvn clean verify
```

São validados:

```text
hospital-system-api
hospital-notification-service
api-gateway
discovery-server
```

O frontend executa operações equivalentes a:

```text
npm ci
npm test
npm run build
```

---

# 31. Objetivo do CI

O objetivo da integração contínua é detectar problemas antes que uma nova versão seja implantada.

Exemplo:

```text
git push
   ↓
GitHub Actions
   ↓
Hospital API ✅
Notification Service ✅
Gateway ✅
Discovery Server ✅
Frontend ✅
```

Caso qualquer projeto apresente erro:

```text
CI ❌
```

a nova versão não deve seguir para as etapas seguintes.

Durante a implementação do projeto, o próprio CI permitiu identificar problemas relacionados a:

- testes;
- inicialização do ApplicationContext;
- configuração de dependências Maven;
- caminhos incorretos de projetos;
- configuração do Java no runner.

---

# 32. Build e publicação das imagens

Uma segunda etapa do pipeline pode utilizar:

```text
.github/workflows/docker-publish.yml
```

para criar automaticamente as imagens Docker após a aprovação do CI.

As imagens dos serviços podem ser publicadas no:

```text
GitHub Container Registry — GHCR
```

utilizando endereços como:

```text
ghcr.io/usuario/hospital-system-api:<versão>
```

Além de `latest`, o identificador do commit pode ser utilizado como tag, permitindo rastrear exatamente qual versão do código originou determinada imagem.

---

# 33. Entrega contínua

O processo de entrega contínua pode ser realizado pelo workflow:

```text
.github/workflows/deploy-kubernetes.yml
```

Após a conclusão bem-sucedida das etapas anteriores, o workflow pode aplicar os manifests e atualizar as imagens executadas pelo Kubernetes.

Exemplo:

```text
CI
 ↓
Docker Build
 ↓
GHCR
 ↓
Kubernetes
 ↓
Rolling Update
```

Para clusters executados localmente, pode ser utilizado um **GitHub Actions Self-hosted Runner** com acesso ao `kubectl` e ao cluster Kubernetes.

---

# 34. Pipeline CI/CD final

O processo completo pode ser representado como:

```text
Desenvolvedor
      │
      │ git push
      ▼
GitHub
      │
      ▼
┌────────────────────────┐
│           CI           │
│                        │
│ Compilação             │
│ Testes                 │
│ Validação frontend     │
└───────────┬────────────┘
            │
            │ sucesso
            ▼
┌────────────────────────┐
│     Docker Build       │
│                        │
│ Criação das imagens    │
│ Publicação no GHCR     │
└───────────┬────────────┘
            │
            ▼
┌────────────────────────┐
│           CD           │
│                        │
│ Kubernetes             │
│ Rolling Update         │
└───────────┬────────────┘
            │
            ▼
     Sistema atualizado
```

Esse processo reduz operações manuais e diminui a possibilidade de que uma versão com erros de compilação ou testes seja implantada.

---

# 35. Arquitetura final

De maneira simplificada, a arquitetura final do Hospital System pode ser representada da seguinte forma:

```text
                    ┌─────────────────┐
                    │     Frontend    │
                    │      React      │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   API Gateway   │
                    └───────┬─────────┘
                            │
                  ┌─────────┴─────────┐
                  │                   │
                  ▼                   ▼
       ┌──────────────────┐    ┌──────────────────┐
       │ Hospital System  │    │   Notification   │
       │       API        │    │     Service      │
       └────────┬─────────┘    └────────┬─────────┘
                │                       │
                ▼                       ▼
          PostgreSQL               PostgreSQL
                │
                │ AppointmentEvent
                ▼
          ┌────────────┐
          │ RabbitMQ   │
          └──────┬─────┘
                 │
                 └───────────────────► Notification Service


            ┌─────────────────────┐
            │ Discovery Server    │
            │       Eureka        │
            └─────────────────────┘
```

A infraestrutura pode ser executada utilizando Docker e orquestrada com Kubernetes, enquanto métricas, logs e traces podem ser centralizados pela camada de observabilidade.

---

# 36. Considerações finais

A evolução do Hospital System permitiu aplicar conceitos importantes de engenharia de software em diferentes níveis da aplicação.

O sistema evoluiu de uma aplicação com dados armazenados temporariamente para uma arquitetura com persistência permanente e separação clara das responsabilidades.

A utilização de conceitos de DDD permitiu identificar o domínio principal, os subdomínios e seus respectivos bounded contexts.

A introdução do Notification Service permitiu separar uma nova responsabilidade da aplicação principal, enquanto Eureka e API Gateway contribuíram para a comunicação e descoberta dos microsserviços.

Posteriormente, a substituição da criação síncrona de notificações por eventos publicados no RabbitMQ aumentou o desacoplamento e a resiliência da arquitetura.

Docker e Kubernetes forneceram uma base para empacotamento, implantação, persistência e escalabilidade dos serviços.

A utilização de Actuator, Micrometer e OpenTelemetry preparou a aplicação para monitoramento de métricas e rastreamento distribuído.

Por fim, Git, GitHub e GitHub Actions permitiram automatizar a validação da aplicação por meio de um processo de integração e entrega contínua.

Como resultado, o projeto passou a contemplar diferentes características encontradas em arquiteturas modernas de microsserviços, incluindo:

- Domain-Driven Design;
- bounded contexts;
- persistência com PostgreSQL;
- auditoria de dados;
- microsserviços independentes;
- service discovery;
- API Gateway;
- comunicação REST;
- comunicação orientada a eventos;
- RabbitMQ;
- idempotência;
- Docker;
- Kubernetes;
- armazenamento persistente;
- observabilidade;
- testes automatizados;
- Git e GitHub;
- CI/CD com GitHub Actions.

A arquitetura resultante apresenta maior separação de responsabilidades, menor acoplamento entre os serviços e melhores condições para manutenção, monitoramento, implantação e evolução futura do sistema.
