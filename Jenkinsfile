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
            checkout changelog: false, poll: false, scm: [$class: 'SubversionSCM', filterChangelog: false, ignoreDirPropChanges: false, locations: [[cancelProcessOnExternalsFail: false, depthOption: 'infinity', ignoreExternalsOption: true, local: '.']], quietOperation: false, workspaceUpdater: [$class: 'UpdateWithRevertUpdater']]
          }
      }
      stage("Build") {
          steps {
              withGradle {
                sh './gradlew clean ftpDist --no-daemon --refresh-dependencies'
              }
          }
      }
//       stage('Test'){
//           steps {
//               splitTests {
//                 sh './gradlew test'
//                 junit '**/build/test-results/test/*.xml'
//               }
//           }
//       }
      
    stage('Test') {
        steps {
//             sh './gradlew test'
//             junit '**/build/test-results/test/*.xml'
            testtest()
            PrintStage()
            runTest()
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

void testtest(){
}

void PrintStage(String text=""){
    text=="" ? println ('* '*10 + env.STAGE_NAME.toUpperCase() + " *"*10) : println (text)
}
void runTests(def args) {
  /* Request the test groupings.  Based on previous test results. */
  /* see https://wiki.jenkins.io/display/JENKINS/Parallel+Test+Executor+Plugin and demo on github
  /* Using arbitrary parallelism of 4 and "generateInclusions" feature added in v1.8. */
  def splits = splitTests parallelism: [$class: 'CountDrivenParallelism', size: 4], generateInclusions: true

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
        checkout scm



        def mavenInstall = './gradlew test'

        /* Write includesFile or excludesFile for tests.  Split record provided by splitTests. */
        /* Tell Maven to read the appropriate file. */
        if (split.includes) {
          writeFile file: "target/parallel-test-includes-${i}.txt", text: split.list.join("\n")
          mavenInstall += " -Dsurefire.includesFile=target/parallel-test-includes-${i}.txt"
        } else {
          writeFile file: "target/parallel-test-excludes-${i}.txt", text: split.list.join("\n")
          mavenInstall += " -Dsurefire.excludesFile=target/parallel-test-excludes-${i}.txt"
        }

        /* Call the Maven build with tests. */
        sh 'mavenInstall'

        /* Archive the test results */
        junit '**/target/surefire-reports/TEST-*.xml'
      }
    }
  }
  parallel testGroups
}
