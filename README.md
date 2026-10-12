# Apothic Progression 1.0.1

Nova progressão em **pontos de XP**, não em níveis da barra do jogador:

`C(n) = 10 × n³`

Preço por operação: soma de `max(0, C(novo) − C(atual))` para cada encantamento do item. O nível atual é o do item que está sendo aprimorado, não o de um livro do segundo slot. Transferir um livro para um item também cobra os níveis adicionados ao item. Encantamentos existentes não geram cobrança retroativa. Remoções/rebaixamentos não devolvem XP nem subsidiam outros encantamentos.

| Nível | XP acumulado por encantamento desde zero |
|---|---:|
| 20 | 80.000 |
| 50 | 1.250.000 |
| 70 | 3.430.000 |
| 98 | 9.411.920 |
| 127 | 20.483.830 |

126 → 127 custa **480.070 XP**. Aprimorar gradualmente ou diretamente chega ao mesmo custo acumulado. Vários encantamentos somam seus preços.

## Integrações

- **Enchanting Infuser:** preço calculado da seleção completa, saldo em pontos, validação no servidor antes de modificar o item, desconto único. Botão abreviado e tooltip com preço completo. Reparo continua com sua regra original.
- **Mesa do Apothic:** preço da lista determinística de encantamentos ofertados; servidor transmite preços em quatro palavras de 16 bits por opção. Recalcula e valida antes de encantar. Tooltip mostra pontos e fica vermelho sem saldo. Lápis, requisitos de potência/nível e receitas de infusão mantêm suas regras existentes. Receitas de infusão usam o custo original em XP, pois não são aumentos de nível de encantamento.
- **Bigorna:** novas adições/aumentos seguem a fórmula, com checagem de retirada e preço em XP. Renomear/reparar sem aumentar encantamentos preserva a cobrança original do Apothic. O pagamento substitui o desconto anterior, preservando consumo de materiais, desgaste da bigorna e hooks do NeoForge.
- Criativo continua sem pagar.

Não há medição de produção de farms, taxa R ou tarefa periódica de monitoramento. A mesa reutiliza a geração das ofertas; o Infuser calcula sobre os encantamentos selecionados. Consultar o saldo usa a fórmula fechada dos níveis do jogador; debitar usa busca binária, sem laço proporcional aos milhões de pontos. A fórmula usa inteiros de 64 bits. O campo histórico `totalExperience` não é usado para decidir saldo.

## Instalação

JAR: `mods/apothic-progression-1.21.1-1.0.1.jar`.

Instalar **no servidor/host e em todos os clientes**, junto do mesmo modpack. Reiniciar Minecraft/servidor. Dependências fixadas nas versões auditadas: Minecraft 1.21.1, NeoForge 21.1.249+, Apothic Enchanting 1.6.1, Enchanting Infuser 21.1.4 e Placebo 9.9.2. MixinExtras 0.5.3 é fornecido pelo NeoForge 21.1.249.

A configuração de cap/potência e o mod de cores existentes são independentes e continuam necessários. Para desfazer apenas esta mudança, fechar o jogo e remover este novo JAR; ele não registra blocos, itens ou dados próprios no mundo.

## Limites e validação

As máquinas automáticas do **Create Enchantment Industry**, incluindo Blaze Enchanter/Forger, usam experiência líquida e capacidade de tanque próprias e **não foram alteradas**. Portanto, esta mudança não garante uma economia fechada em todos os meios de obtenção do modpack. Aplicar milhões de XP às máquinas exige definir consumo gradual/armazenamento, em vez de apenas substituir um número e tornar as receitas impossíveis com o tanque atual.

Loot, comandos e livros já existentes também não são cobrados na obtenção. Bibliotecas continuam limitadas a 16/31; a biblioteca nova foi adiada. A regra de cores 128+ permanece intacta.

Validação automatizada: compilação Java 21; valores-chave e soma de todos os upgrades até 127; 25.000 níveis de jogador comparados à soma das parcelas vanilla; 100.000 casos de conversão/desconto sem perda de pontos; transmissão de 64 bits por pacotes de 16 bits; verificação ASM de 19 pontos de integração, assinaturas e chamadas nos JARs instalados. Resultados em `build/test-results.txt` e `build/target-results.txt`.

**Não foi executado um teste dentro de um mundo nem iniciado outro Minecraft.** A aplicação real dos mixins e a apresentação precisam da próxima inicialização do jogo. Para conferir: testar falta de saldo, saldo exato, aprimoramento 126→127, mais de um encantamento, remoção sem reembolso e as três interfaces. Os requisitos de potência continuam aplicáveis.

## Recompilar

Executar `python .codex-maintenance/2026-09-08-apothic-progression/build.py` nesta instância, com Java/Javac 21. O build usa as dependências locais e a cópia de MixinExtras extraída do NeoForge apenas para compilar; não embute nem instala outra versão da biblioteca.


## Interface 1.0.1

O tooltip de custo do Infuser e da mesa mostra a equivalência desde nível zero da barra do jogador. Exemplo: 2.710 XP equivalem ao nível 38 com aproximadamente 90,8% da barra até o 39. Partir do nível 39 com a barra vazia é suficiente (2.727 XP acumulados).

Se houver saldo, mostra a barra atual e a barra após o pagamento; se não houver, mostra quantos pontos faltam. A equivalência desde zero não significa que esse número de níveis será subtraído do nível atual: a curva de XP do jogador não é linear. As porcentagens são aproximações com uma casa decimal, enquanto a cobrança mantém a precisão em pontos. Os números seguem o idioma do jogo (pt_br ou en_us).

Esta atualização altera somente a apresentação. Remover o JAR 1.0.0 ao instalar 1.0.1; manter uma única versão em mods. O backup anterior está em backup-before-1.0.1.
