mvn_setting_path := "/e/devTool/repo/settings.xml"
sky_path := "."

# show list
a:
    @just -l

# deploy to maven center
publish:
   mvn -s {{mvn_setting_path}} -f  {{sky_path}}/pom.xml -DskipTests=true clean deploy