# GAMAFLIX1 v1.6 — Android TV

Projeto Android TV **Kotlin + Jetpack Compose**, redesenhado a partir das características de layout observadas no APK de referência fornecido pelo proprietário. Não incorpora código, scripts, imagens ou demais ativos extraídos do APK de referência.

## Visual e navegação

- Fundo preto, detalhes em vermelho (#E50914), textos brancos.
- Logo **GAMA** e vinheta centralizada (palavra GAMA → símbolo G).
- Navegação superior compacta: Início, TV, Filmes, Séries, Categorias, EPG, Recentes, Minha Lista, Configurações.
- Início com destaque e trilhos horizontais de conteúdo, identificados a partir da playlist do usuário.
- Catálogos organizados por `group-title` em uma grade de categorias, antes da listagem de itens.
- Telas de conteúdo com seleção pelo controle remoto e opção de abrir em player externo.
- XMLTV: informações de programação atual/futura, sinopses, data e classificação quando esses dados constarem no arquivo.

## Catálogo grande

A importação M3U ocorre por streaming para um banco SQLite em vez de carregar todos os registros na memória. Não há limite fixo de 10.000 itens; capacidade e desempenho dependem do armazenamento do dispositivo. Paginação de 60 conteúdos por categoria. A home consulta apenas 12 itens por tipo.

## GitHub Dev

1. No repositório `felipegama09/GAMAFLIX1`, abra `https://github.dev/felipegama09/GAMAFLIX1`.
2. Extraia o ZIP; arraste **seu conteúdo** ao Explorador na raiz do projeto (não a pasta que contém tudo).
3. Substitua os arquivos existentes e confira os caminhos `app/src/main/...`.
4. Faça **Commit e Push** no Controle do Código-Fonte.
5. Em **GitHub → Actions**, use **Build GAMAFLIX1 APK**. O artefato será `GAMAFLIX1-apk`.

## Instalação ADB (Windows PowerShell)

```powershell
$adb = 'C:\ADB\platform-tools\adb.exe'
$tv = '192.168.18.5:5555'
$apk = 'C:\Users\FelipePereira\Downloads\GAMAFLIX1-apk\app-debug.apk'
& $adb connect $tv
& $adb -s $tv install -r $apk
& $adb -s $tv shell monkey -p com.gamaflix.tv 1
```

## Notas

- O APK de referência é uma aplicação web encapsulada com Capacitor, enquanto este projeto é um aplicativo nativo Compose com visual inspirado na organização da interface fornecida. Não foi realizada uma reconstrução bit a bit do APK.
- Listas M3U e XMLTV são endereços de configuração que podem conter credenciais. Mantenha o repositório privado e, para distribuição, remova os valores pré-preenchidos, peça-os ao usuário e armazene-os com mais proteção.
- A renderização de imagens de catálogo depende de URLs disponíveis na playlist. Não são adicionados catálogos fictícios.
- A compatibilidade de codecs depende do aparelho e do stream. O EPG exige coincidência entre o `tvg-id` do M3U e o identificador do canal no XMLTV.
- **A compilação no GitHub Actions e os testes na TV ainda precisam ser realizados.**
