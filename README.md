# cadence-java-client-starter
This is a sample of Spring starter for [Cadence client](https://github.com/uber/cadence-java-client).

## Starter architecture
This starter requires no changes on [Cadence client](https://github.com/uber/cadence-java-client) side. 
Starter makes from Cadence workflows classes Spring beans. 

This is archived by next steps:
  * Create CGLIB proxy around Workflow class. This makes *Cadence workflow* a singleton Spring bean.
  * Add callback to CGLIB proxy.
  * Callback has a logic to create specific Cadence workflow stub around original Workflow Spring bean.
  * The original Spring has Prototype scope. So new instance of bean will be created for each workflow call.   

## Usage

 * Create Spring Boot application and mark it by @EnableCadence from this starter.
 * Create Cadence your [Workflow](Workflow) and mark it with @Workflow.
 * @Workflow has an argument of workflow taskList name
 * Autowire defined workflow to any class and use it as ordinal Spring bean.
 
 See sample in [unit test](com.uber.cadence.client.starter.app.AutoConfigurationTest)  