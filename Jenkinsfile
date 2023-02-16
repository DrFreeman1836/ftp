//@Library('testInParallel') _
pipeline {
    agent any

  stages {
       stage('Hello') {
          steps {
            echo 'Hello World'
         }
       }
      stage("Checkout") {
          steps {
            PrintStage()
            checkout scm
            //checkout changelog: false, poll: false, scm: [$class: 'SubversionSCM', filterChangelog: false, ignoreDirPropChanges: false, locations: [[cancelProcessOnExternalsFail: false, depthOption: 'infinity', ignoreExternalsOption: true, local: '.']], quietOperation: false, workspaceUpdater: [$class: 'UpdateWithRevertUpdater']]
          }
      }
      stage("Build") {
          steps {
              withGradle {
                sh './gradlew clean ftpDist --no-daemon --refresh-dependencies'
                //sh './gradlew build'
              }
          }
      }
//       stage('Test'){
//           steps {
//               PrintStage()
//               splitTests {
//                 parallelism.count = 5
//                 sh './gradlew test'
//                 junit '**/build/test-results/test/*.xml'
//               }
//           }
//       }
      
    stage('Test') {
        steps {
            PrintStage()
            //sh './gradlew test'
            //junit '**/build/test-results/test/*.xml'
            //runTests()
            customRunTest()
            //needRunParallelTest()
            //test2()
        }
    }
      
//           stage('parallel test'){
//         parallel{
//             stage('test 1'){
//                 steps {
//                     sh './gradlew test'
//                 }
//             }
//             stage('test 2'){
//                 steps{
//                     sh './gradlew test'
//                 }
//             }
//         }
//     }
      
  }
  post {
    always {
        emailext(body: '${DEFAULT_CONTENT}', mimeType: 'text/html',
                 replyTo: '$DEFAULT_REPLYTO', subject: '${DEFAULT_SUBJECT}',
                 to: emailextrecipients([[$class: 'CulpritsRecipientProvider'],
                                         [$class: 'RequesterRecipientProvider']]))
    }
  }
}

void PrintStage(String text=""){
    text=="" ? println ('* '*10 + env.STAGE_NAME.toUpperCase() + " *"*10) : println (text)
}

void needRunParallelTest() {
def splits = splitTests([$class: 'CountDrivenParallelism', size: 2])
def branches = [:]
for (int i = 0; i < splits.size(); i++) {
  def exclusions = splits.get(i);
  branches["split${i}"] = {
      writeFile file: 'exclusions.txt', text: exclusions.join("\n")//split.list.join
      //sh "./gradlew -I ./exclusions.gradle clean check"
      sh "./gradlew -Dsurefire.excludesFile=exclusions.txt"
      step([$class: 'JUnitResultArchiver', testResults: 'build/test-results/*.xml'])//-Dsurefire.excludesFile= //./gradlew test --tests "com.xyz.b.module.TestClass.testToRun"
  }
}
parallel branches
}

void customRunTest() {
    //def splits = splitTests parallelism: count(4), generateInclusions: true
    def splits = splitTests parallelism: [$class: 'CountDrivenParallelism', size: 2], generateInclusions: false
    for (int i = 0; i < splits.size(); i++) {
        def split = splits[i]
        echo 'iteration i'
        //echo "splits[${i}]: includes=${split.includes} list=${split.list}"
        println(splits[i])
    }
    sh './gradlew test'
    junit '**/build/test-results/test/*.xml'
}

void runTests() {
  /* Request the test groupings.  Based on previous test results. */
  /* see https://wiki.jenkins.io/display/JENKINS/Parallel+Test+Executor+Plugin and demo on github
  /* Using arbitrary parallelism of 4 and "generateInclusions" feature added in v1.8. */
  def splits = splitTests parallelism: [$class: 'CountDrivenParallelism', size: 2], generateInclusions: true

  /* Create dictionary to hold set of parallel test executions. */
  def testGroups = [:]

  for (int i = 0; i < splits.size(); i++) {
    def split = splits[i]

    /* Loop over each record in splits to prepare the testGroups that we'll run in parallel. */
    /* Split records returned from splitTests contain { includes: boolean, list: List<String> }. */
    /*     includes = whether list specifies tests to include (true) or tests to exclude (false). */
    /*     list = list of tests for inclusion or exclusion. */
    /* The list of inclusions is constructed based on results gathered from */
    /* the previous successfully completed job. One additional record will exclude */
    /* all known tests to run any tests not seen during the previous run.  */
    testGroups["split-${i}"] = {  // example, "split3"
      node {
        //checkout scm
        def run = './gradlew test'

        /* Write includesFile or excludesFile for tests.  Split record provided by splitTests. */
        /* Tell Maven to read the appropriate file. */
        if (split.includes) {
          writeFile file: "build/parallel-test-includes-${i}.txt", text: split.list.join("\n")
          run += " -Dsurefire.includesFile=build/parallel-test-includes-${i}.txt"
        } else {
          writeFile file: "build/parallel-test-excludes-${i}.txt", text: split.list.join("\n")
          run += " -Dsurefire.excludesFile=build/parallel-test-excludes-${i}.txt"
        }

        /* Call the Maven build with tests. */
          echo '================================================================'
          echo run
        //

        /* Archive the test results */
        sh run
        junit '**/build/test-results/test/*.xml'
      }
    }
  }
  parallel testGroups
}

void test2() {
def stepsForParallelTest = [:]
// if (canFindPreviousTestResult()) {
    def splits = splitTests parallelism: count(2), generateInclusions: true
    for (int i = 0; i < splits.size(); i++) {
        def split = splits[i]
        stepsForParallelTest["java-${i}"] = parallelDynamicTestStep(i, split)
    }
// } else {
//     def testProfiles = ["test-1", "test-2", "test-3"]
//     for (def i = 0; i < testProfiles.size(); i++) {
//         def profile = testProfiles[i]
//         stepsForParallelTest[profile] = parallelFixedTestStep(profile)
//     }
// }
parallel stepsForParallelTest
}
