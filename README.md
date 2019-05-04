# MongoFly

Mongofly é um starter do Spring Boot para executar scripts do MongoDB. Ele é inspirado na forma de funcionamento do Flyway.

## Como utilizar

Atualmente o projeto encontra-se apenas no repositório JitPack e funciona apenas em projetos já configurados com o Spring Data MongoDB.

### Pré requisitos

Esse starter utiliza o MongoDB já configurado no projeto para executar os scripts. Por isso é necessário que você já tenha configurado o Spring Data MongoDB no projeto informando qual é o banco e outras informações que forem relevantes ao seu projeto.

### Configurando o Mongofly em seu projeto

Atualmente o Mongofly encontra-se apenas no repostório de projetos JitPack, por isso é necessário ensinar ao seu gerenciador de dependencias que deve buscar bibliotecas de lá também.

No caso do Maven ficará assim:

```
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
```

E no Gradle

```
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Para outros gerenciadores de dependencias consultar a página do projeto no JitPack: https://jitpack.io/#flaviochess/mongofly

Também é necessário criar uma pasta chamada `mongofly` na raiz do seu projeto, onde ficarão armazenado os scripts do MongoDB que devem ser executados. Assim que o projeto for iniciado, o Mongofly irá verificar na collection `mongofly` os arquivos que ainda não foram executados com sucesso e executar-los.

## Criando os scripts

Os scripts ficarão dentro da pasta `mongofly`. A seguir as indicações de como o Mongofly espera estes arquivos.

### Formato do arquivo

Os scripts devem ser arquivos do tipo json. Dentro de cada json pode ter um ou muitos comandos do MongoDB.

O nome do arquivo deve seguir a nomeclatura: versao__nome_de_sua_preferencia.json, onde a versão deve sempre iniciar com a letra "v" e seguir com dois underscore `_` após a versão. Exemplos de nomes de arquivos válidos seriam:

```
v1__meu_primeiro_script.json

v2__meu-segundo-script.json

v3__meuTerceiroScript.json
```

Os arquivos serão executados em ordem crescente de versão e por isso as versões não devem ser repetidas para evitar que sejam executados em uma ordem indesejada.

Apenas são aceitos comandos que utilizam a nomeclatura padrão do MongoDB, as que normalmente se iniciam por "db.collection.operation", por exemplo: `db.users.insert(...);`

Todo commando deve encerrar com ponto e virgula `;`.

Os comandos podem ser separados dentro do arquivo por linhas em branco se assim preferir. Também é permitido usar quebras de linha em um commando para identar o mesmo, não há restrições quanto a formatação dos arquivos.

A seguir um exemplo de conteúdo de um arquivo válido, o arquivo se chama v1__add_users_and_change_felipe_password.json:

```
db.users.insert({
    "name": "Flavio",
    "login": "flaviochess",
    "password": "123",
    "age": 28
});

db.users.insert({
    "name": "Jairo",
    "login": "jairovsky",
    "password": "!@#",
    "age": 24
});

db.users.update(
    {"login" : "Felipe"}, 
    {$set: 
        {"password": "mudar@123"}
    }
);
```

## Comandos aceitos

Este projeto teve como ideia inicial atender uma demanda pessoal, por isso não foram incluídos todos os comandos ou até mesmo parametros aceitos pelo MongoDB, mas acredito que atenda uma necessidade em geral. Na versão atual o projeto aceita basicamente insert, update e remove com algumas ressalvas quanto a parametros extras em alguns deles. Veja a seguir.

### Insert

É aceito apenas o commando `insert`, logo suas variações como `insertMany` ou `insertOne` não são suportadas ainda. Porém é aceito insert com um documento ou um array de documentos assim como o comando insert do MongoDB, reduzindo ou até mesmo eximando a necessidade de usar as variações do insert.

Os parâmetros extras como `ordered` e `writeConcern` são aceitos.

Documentação do MongoDB do Insert: https://docs.mongodb.com/manual/reference/method/db.collection.insert/

### Update

Assim como no caso do insert, é aceito apenas o comando `update` mas não as suas variações ainda.

Os parâmetros extras `multi` e `writeConcern` são aceitos.

Os parâmetros extras `upsert`, `collation` e `arrayFilters` **não** são aceitos ainda.

Modificadores utilizados no meio do comando update como `$set`, `$addToSet` e `$each` não deveriam causar nenhum problema, mas até o momento foi testado apenas com os três citados. De qualquer forma não existe nenhum desenvolvimento específico para estes, por isso não deveria haver problemas com outros.

Documentação do MongoDB do Update: https://docs.mongodb.com/manual/reference/method/db.collection.update/

### Remove

O remove não aceita **nenhum** dos parâmetros extras ainda, ou seja: `justOne`, `writeConcern` e `collation`.

## Como funciona o Mongofly

Sempre que o projeto for inciado o Mongofly irá verificar os arquivos da pasta `mongofly` e verificar quais ainda não foram executados, olhando a *collection* também de nome `mongofly`. Ao termino da execução de cada arquivo é criado ou atualizado um documento na *collection* `mongofly` informando se o arquivo executou com sucesso ou não. Um exemplo seria:

```
{
    "version": 1,
    "script": "v1__add_users_and_change_felipe_password.json",
    "executedOn": "2019-04-24 16:27:21",
    "success": true
}
```

Se ocorrer um erro, será lançado uma exceção que interromperá o start da aplicação e inserido um documento com o campo `success` como `false`. Nestes casos é possível corrigir o script e executar a aplicação novamente. Neste momento o Mongofly irá ignorar os scritps que estão como `success = true` pois já foram executados com exito e continuará a partir do `success = false`. Caso agora o script esteja correto, o documento será atualizado para `true` e continuará a execução dos demais arquivos, se houver.

A *collection* `mongofly` será criada apenas após a execução do primeiro arquivo.

## Principais Tecnologias

* [Java 8](https://www.oracle.com/technetwork/pt/java/javase/downloads/jdk8-downloads-2133151.html) - Linguagem de programação
* [Spring Boot](https://spring.io/projects/spring-boot) - Framework de desenvolvimento
* [Maven](https://maven.apache.org/) - Gerenciador de dependencias

## Contribuição

Para contribuir com este projeto por favor leio o [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) para mais detalhes de como o projeto está estruturado, do nosso código de conduta e os processos de como nos enviar pull requests.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

