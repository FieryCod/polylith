GRAALVM_HOME=~/.graalvm/bin

# Recipe for building native version of polylith tool

.PHONY: clean install-local

all: poly

clean:
	@rm -Rf poly.jar poly

poly.jar:
	@cd projects/poly && \
	clojure -X:uberjar \
            :aot [polylith.clj.core.poly-cli.core] \
            :jvm-opts '["-Dclojure.compiler.direct-linking=true", "-Dclojure.spec.skip-macros=true"]' \
            :main-class polylith.clj.core.poly-cli.core \
            :jar ../../poly.jar

# >= GraalVM 21.2.0
poly: poly.jar
	@$(GRAALVM_HOME)/native-image -jar poly.jar \
	--no-server \
    --verbose \
    --no-fallback \
    --allow-incomplete-classpath \
	--report-unsupported-elements-at-runtime \
	-H:EnableURLProtocols=http,https,jar \
	-H:ReflectionConfigurationFiles=reflect-config.json \
    -H:ResourceConfigurationFiles=resources-config.json \
	-J-Dclojure.spec.skip-macros=true \
	-J-Dclojure.compiler.direct-linking=true \
    --initialize-at-build-time="clojure,polylith.clj.core.workspace_clj,sci.impl,polylith.clj.core.lib,polylith.clj.core.workspace.text_table.ws_table_column,malli,polylith.clj.core.validator,rewrite_clj.node,zprint,polylith.clj.core.help,rewrite_clj.parser,polylith.clj.core.ws_file,polylith.clj.core.command,polylith.clj.core.path_finder,polylith.clj.core.util,polylith.clj.core.text_table,polylith.clj.core.migrator,polylith.clj.core.util.interface,polylith.clj.core.deps,polylith.clj.core.command.cmd_validator,polylith.clj.core.creator,polylith.clj.core.user_input,polylith.clj.core.common,edamame,polylith.clj.core.ws_explorer,polylith.clj.core.file,rewrite_clj.zip,polylith.clj.core.workspace.text_table,rewrite_clj.custom_zipper,polylith.clj.core.workspace,polylith.clj.core.git,polylith.clj.core.validator.datashape,polylith.clj.core.deps.text_table,polylith.clj.core.path_finder.interface,edamame.impl,puget,malli.impl,polylith.clj.core.test_runner,polylith.clj.core.poly_cli,arrangement,fipp,me.raynes,polylith.clj.core.change,puget.color,sci,rewrite_clj,sci.impl.parser.edamame,polylith.clj.core.lib.text_table,polylith.clj.core.user_config,polylith.clj.core.shell,borkdude,sci.impl.parser,polylith.clj.core.version,cognitect.aws" && \
	chmod +x ./poly

install-local: poly poly.jar
	chmod +x poly && \
	chmod +x poly.jar && \
    chmod +x poly-jar && \
    sudo mkdir -p /usr/local/polylith && \
    sudo cp poly.jar /usr/local/polylith/ && \
	sudo cp poly-jar /usr/local/bin/ && \
	sudo cp poly /usr/local/bin/

test:
	./poly check && \
	./poly deps && \
    ./poly diff && \
    ./poly help && \
    ./poly info && \
    ./poly libs && \
    ./poly version && \
    ./poly ws && \
	./poly test && \
    ./poly ws && \
    ./poly ws get:keys && \
    ./poly ws get:count && \
    ./poly ws get:settings && \
    ./poly ws get:user-input:args && \
    ./poly ws get:user-input:args:0 && \
    ./poly ws get:settings:keys && \
    ./poly ws get:components:keys && \
    ./poly ws get:components:count && \
    ./poly ws get:components:mycomp:lines-of-code && \
    ./poly ws get:settings:vcs:polylith :latest-sha && \
    ./poly ws get:settings:vcs:polylith :latest-sha branch:master
