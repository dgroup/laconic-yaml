[![Maven](https://img.shields.io/maven-central/v/io.github.dgroup/tagyml.svg)](https://mvnrepository.com/artifact/io.github.dgroup/tagyml)
[![Javadocs](http://www.javadoc.io/badge/io.github.dgroup/tagyml.svg)](http://www.javadoc.io/doc/io.github.dgroup/tagyml)
[![License: MIT](https://img.shields.io/github/license/mashape/apistatus.svg)](./license.txt)
[![Commit activity](https://img.shields.io/github/commit-activity/y/dgroup/tagyml.svg?style=flat-square)](https://github.com/dgroup/tagyml/graphs/commit-activity)
[![Hits-of-Code](https://hitsofcode.com/github/dgroup/tagyml)](https://hitsofcode.com/view/github/dgroup/tagyml)

[![Build Status](https://travis-ci.org/dgroup/tagyml.svg?branch=master&style=for-the-badge)](https://travis-ci.org/dgroup/tagyml)
[![0pdd](http://www.0pdd.com/svg?name=dgroup/tagyml)](http://www.0pdd.com/p?name=dgroup/tagyml)
[![Dependency Status](https://requires.io/github/dgroup/tagyml/requirements.svg?branch=master)](https://requires.io/github/dgroup/tagyml/requirements/?branch=master)
[![Known Vulnerabilities](https://snyk.io/test/github/dgroup/tagyml/badge.svg)](https://snyk.io/org/dgroup/project/58b731a9-6b07-4ccf-9044-ad305ad243e6/?tab=dependencies&vulns=vulnerable)

[![DevOps By Rultor.com](http://www.rultor.com/b/dgroup/tagyml)](http://www.rultor.com/p/dgroup/tagyml)
[![EO badge](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org/#principles)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Qulice](https://img.shields.io/badge/qulice-passed-blue.svg)](http://www.qulice.com/)
[![SQ maintainability](https://sonarcloud.io/api/project_badges/measure?project=io.github.dgroup%3Atagyml&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=io.github.dgroup%3Atagyml)
[![Codebeat](https://codebeat.co/badges/f61cb4a4-660f-4149-bbc6-8b66fec90941)](https://codebeat.co/projects/github-com-dgroup-tagyml-master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b7e76567bb99484a9bff9d2ff1ff4be3)](https://www.codacy.com/app/dgroup/tagyml?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=dgroup/tagyml&amp;utm_campaign=Badge_Grade)
[![Codecov](https://codecov.io/gh/dgroup/tagyml/branch/master/graph/badge.svg?token=Pqdeao3teI)](https://codecov.io/gh/dgroup/tagyml)

**ATTENTION**: We're still in a very early alpha version, the API
may and _will_ change frequently. Please, use it at your own risk,
until we release version 1.0.

Maven:
```xml
<dependency>
    <groupId>io.github.dgroup</groupId>
    <artifactId>tagyml</artifactId>
</dependency>
```
Gradle:
```groovy
dependencies {
    compile 'io.github.dgroup:tagyml:<version>'
}
```

### Get started 
**Represents the YAML file as an object `Teams`**

The examples below are built on top of [snakeyaml](https://bitbucket.org/asomov/snakeyaml) framework, but you can override it 
by using the [extension point](/src/main/java/io/github/dgroup/tagyml/Yaml.java) in favour of your favourite framework.

 1. Define YAML [file](/src/test/resources/yaml/teams.yaml) for version `1.0`.
    ```yaml
    version: 1.0

    teams:

      - id: 100
        name:     BSS dream team
        lead:
          name:   Bob Marley
          email:  bob.marley@dreamteam.com
        properties:
          total-devs: 5
          total-managers: 10

      - id: 200
        name:     OSS dream team
        lead:
          name:   Jules Verne
          email:  jules.verne@dreamteam.com
        properties:
          total-devs: 5
          total-managers: 5
    ```
    in
    ```
    tagyml $ tree
    ...
    |-- src
    |   |-- main
    |   |   |-- ...
    |   |
    |   `-- test
    |       |-- java
    |       |   `-- ...
    |       `-- resources
    |           `-- yaml
    |               |-- teams.yaml
    |               |-- ...
    ...
    ```
 2. Define the interfaces for the domain model
    ```java
    /** 
     * YAML file-based team.
     */
    public interface Team {
        int id() throws YamlFormatException;
        String name() throws YamlFormatException;
        User lead() throws YamlFormatException;
        Properties properties() throws YamlFormatException;
    }
    
    /**
     * YAML file-based user.
     */
    public interface User {
        String name();
        String email();
    }
    ```
    See [UncheckedYamlFormatException](/src/main/java/io/github/dgroup/tagyml/UncheckedYamlFormatException.java) in case if you don't wan't to throw the checked exception during YAML file parsing procedure.

 3. Define the YAML parsing process
    ```java
    public class Teams implements Scalar<Iterable<Team>> {

        private static final Logger LOG = LoggerFactory.getLogger(Teams.class);

        /**
         * @param path The path to the YAML file.
         * @param charset The charset of YAML file to read.
         */
        public Teams(final Path path, final Charset charset) {
            this.path = path;
            this.charset = charset;
        }
        
        @Override
        public Iterable<Team> value() throws YamlFormatException {
            // Parse YAML file using first supported YAML format
            return new FirstIn<>(
                // Print to logs the details about failed attempt of
                // parsing YAML file using a particular format.
                (version, exception) -> LOG.warn(
                    new FormattedText(
                        "Unable to parse '%s' using '%s' format",
                        this.path, version
                    ).asString(),
                    exception
                ),
                // The exception message in case if YAML file can't be
                // parsed using any specified formats below
                new FormattedText(
                    "The file %s has unsupported YAML format", this.path
                ),
                // The supported YAML formats
                new TeamsV1(this.path, this.charset),
                new TeamsV2(this.path, this.charset),
                ...
                new TeamsVn(this.path, this.charset)
            ).value();
        }
    }
    ```
    See [FirstIn](/src/main/java/io/github/dgroup/tagyml/format/FirstIn.java).

 4. Define YAML format for version 1.0 - `TeamsV1`
    ```java
    final class TeamsV1 implements Format<Iterable<Team>> {
    
        private final Path path;
        private final Charset charset;
    
        TeamsV1(final Path path, final Charset charset) {
            this.path = path;
            this.charset = charset;
        }

        @Override
        public String version() {
            return "1.0";
        }

        @Override
        public Iterable<Team> value() throws YamlFormatException {
            return new Snakeyaml(YamlFile.class)
                .apply(new YamlText(this.path, this.charset))
                .tagTeams();
        }
    
        /**
         * The object which represents YAML file.
         * The YAML file should have at least `version` and collection of `team`.
         * The EO concept is against getters/setters, but we need them in order to
         *  parse the file by snakeyaml lib, thus, once we completed the parsing
         *  procedure, we should restrict any possible access to such type of objects.
         */
        private static class YamlFile {
            public double version;
            public List<YmlTeam> teams;
    
            public double tagVersion() throws YamlFormatException {
                return new Version(this.version).value();
            }
    
            public Iterable<Team> tagTeams() throws YamlFormatException {
                return new Mapped<>(
                    YamlTeam::tagTeam, new TagOf<>("teams", this.teams).value()
                );
            }
        }
    
        /**
         * Represents 1 element from YAML tag `teams`.
         */
        private static class YmlTeam {
            public int id;
            public String name;
            public YmlTeamMember lead;
            public Map<String, String> properties;
    
            public Team tagTeam() {
                return new Team() {
                    @Override
                    public int id() throws YamlFormatException {
                        return new TagOf<>("id", id).value();
                    }
    
                    @Override
                    public String name() throws YamlFormatException {
                        return new TagOf<>("name", name).value();
                    }
    
                    @Override
                    public User lead() throws YamlFormatException {
                        return new TagOf<>("lead", lead).value().tagUser();
                    }
    
                    @Override
                    public Properties properties() throws YamlFormatException {
                        return new PropertiesOf("properties", properties).value();
                    }
                };
            }
        }
    
        /**
         * Represents YAML tag `lead`
         */
        private static class YmlTeamMember {
            public String name;
            public String email;
    
            public User tagUser() {
                return new User() {
                    @Override
                    public String name() {
                        return new Unchecked(new TagOf<>("name", name)).value();
                    }

                    @Override
                    public String email() {
                        return new Unchecked(new TagOf<>("email", email)).value();
                    }
                );
            }
        }
    }
    ```
    See [Format](src/main/java/io/github/dgroup/tagyml/Format.java), 
    [Snakeyaml](src/main/java/io/github/dgroup/tagyml/yaml/Snakeyaml.java), 
    [Version](src/main/java/io/github/dgroup/tagyml/tag/Version.java), 
    [PropertiesOf](src/main/java/io/github/dgroup/tagyml/tag/PropertiesOf.java), 
    [TagOf](src/main/java/io/github/dgroup/tagyml/tag/TagOf.java) and 
    [Unchecked](src/main/java/io/github/dgroup/tagyml/tag/Unchecked.java).
    
    Instead of [Snakeyaml](src/main/java/io/github/dgroup/tagyml/yaml/Snakeyaml.java) 
    you may implement [Yaml](/src/main/java/io/github/dgroup/tagyml/Yaml.java) 
    using your favourite framework in the same way.
 
 5. Parse YAML file in [EO](http://www.elegantobjects.org/#principles) way
    ```java
    @Test
    public void yamlParsingProcessIsSuccessfull() throws YmlFormatException {
        final Iterable<Team> teams = new Teams(
            Paths.get("src", "test", "resources", "yml", "1.0", "teams.yml")
        ).value();
        MatcherAssert.assertThat(
            "There are 2 teams defined in YAML file",
            teams, Matchers.iterableWithSize(2)
        );
        MatcherAssert.assertThat(
            "The 1st team defined in YAML tag has id 100",
            teams.iterator().next().id(),
            new IsEqual<>(100)
        );
        MatcherAssert.assertThat(
            "The 1st team defined in YAML tag has property `total-dev`",
            teams.iterator().next().properties().getProperty("total-dev"),
            new IsEqual<>(5)
        );
    }
    ```
    See [SnakeyamlTest](/src/test/java/io/github/dgroup/tagyml/yaml/SnakeyamlTest.java)
    for details. Of course, the test above is testing a lot of things and should 
    be separated into bunch of tests with single `MatcherAssert.assertThat` each,
    but here it was done for educational purposes.
