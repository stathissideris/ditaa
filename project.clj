(defproject stathissideris/ditaa "0.11.0"
  :description "command-line utility that can convert diagrams drawn using ascii art into proper bitmap graphics"
  :min-lein-version "2.0.0"
  :url "https://github.com/stathissideris/ditaa"
  :license {:name "GNU Lesser General Public License v3.0"
            :url "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [commons-cli/commons-cli "1.4"]
                 [net.htmlparser.jericho/jericho-html "3.4"]
                 [org.apache.xmlgraphics/batik-gvt "1.9"]
                 [org.apache.xmlgraphics/batik-codec "1.9"]
                 [org.apache.xmlgraphics/batik-bridge "1.9"]
                 [org.apache.xmlgraphics/batik-bridge "1.9"]
                 [org.scilab.forge/jlatexmath "1.0.7"]]
  :main org.stathissideris.ascii2image.core.CommandLineConverter
  :java-source-paths ["src/java"]
  :plugins [[lein-junit "1.1.9"]]
  :junit ["test/java"]
  :junit-formatter :plain
  :junit-results-dir "target/test-results"
  :profiles {:dev {:dependencies      [[junit/junit "4.12"] [com.github.dakusui/thincrest "3.6.0"]]
                   :java-source-paths ["test/java"]}})
