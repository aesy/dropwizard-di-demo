
val hk2Version = "2.5.0-b61"
val dwVersion = "1.3.3"
val psqlVersion = "9.1-901.jdbc3"

plugins {
    java
}

repositories {
    mavenCentral()
}

dependencies {
    compile("postgresql:postgresql:$psqlVersion")
    compile("io.dropwizard:dropwizard-core:$dwVersion")
    compile("io.dropwizard:dropwizard-hibernate:$dwVersion")
    compile("io.dropwizard:dropwizard-migrations:$dwVersion")
    compile("io.dropwizard:dropwizard-auth:$dwVersion")
    compile("org.glassfish.hk2:hk2-api:$hk2Version")
    compile("org.glassfish.hk2:hk2-locator:$hk2Version")
    compile("org.glassfish.hk2:hk2-extras:$hk2Version")
    compile("org.reflections:reflections:0.9.9")
}
