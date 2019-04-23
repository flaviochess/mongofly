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

Também é necessário criar uma pasta chamada `mongofly` na raiz do seu projeto, onde ficaram armazenado os scripts do MongoDB que devem ser executados. Assim que o projeto for iniciado, o Mongofly irá verificar na collection `mongofly` os arquivos que ainda não foram executados com sucesso e executar-los.

## Criando os scripts

Os scripts ficarão dentro da pasta `mongofly`.

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

Os comandos podem ser separados dentro do arquivo por linhas em branco se assim preferir. Também é permitido usar quebras de linha em um commando para identar o mesmo, sem nenhum problema.

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
        {"password": "secret"}
    }
);
```

## Comandos aceitos

Este projeto teve como ideia inicial atender uma demanda pessoal, por isso não foram incluídos todas os comandos ou até mesmo parametros aceitos pelo MongoDB, mas acredito que atenda uma necessidade em geral. Na versão atual o projeto aceita basicamente insert, update e remove com algumas ressalvas quanto a parametros extras em alguns deles.

### Insert

É aceito apenas o commando `insert`, logo suas variações como `insertMany` ou `insertOne` não são suportadas ainda. Porém é aceito insert com um documento ou um array de documentos assim como o comando insert do MongoDB, reduzindo ou até mesmo eximando a necessidade de usar as variações do insert.

Os parâmetros extras como `ordered` e `writeConcern` são aceitos.

Documentação do MongoDB do Update: https://docs.mongodb.com/manual/reference/method/db.collection.insert/

### Update

Assim como no caso do insert, é aceito apenas o comando `update` mas não as suas variações ainda.

Os parâmetros extras `multi` e `writeConcern` são aceitos.

Os parâmetros extras `upsert`, `collation` e `arrayFilters` **não** são aceitos ainda.

Modificadores utilizados no meio do comando update como `$set`, `$addToSet` e `$each` não deveriam ocorrer nenhum problema, mas até o momento foi testado apenas com os três citados. Mas não existe nenhum desenvolvimento específico para estes, por isso não deveria haver problemas com outros.

Documentação do MongoDB do Update: https://docs.mongodb.com/manual/reference/method/db.collection.update/

### Remove

O remove não aceita **nenhum** dos parâmetros extras ainda `justOne`, `writeConcern` e `collation`.


### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

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

