pipeline {
    agent any

  stages {

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
              }
          }
      }
    stage('Test') {
        steps {
            script {
               PrintStage()
               //needRunParallelTest()
               int countForks = 4
               sh "./gradlew parallelTest -Pcount=${countForks}"
               junit '**/build/test-results/test/*.xml'
            }    
        }
    }
      
//     stage('parallel test'){
//         parallel{
//             stage('test 1'){
//                 steps {
//                     sh './gradlew test'
//                 }
//             }
//             stage('test 2'){
//                 steps{
//                     sh './gradlew test2'
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

String getTestName(String className) {
    return className.substring(className.indexOf('/')+1, className.indexOf('.'))
}

void needRunParallelTest() {
def splits = splitTests parallelism: count(2), generateInclusions: false//, estimateTestsFromFiles: false
def branches = [:]
for (int i = 0; i < splits.size(); i++) {
  def split = splits.get(i);
  println(split)
  def run = './gradlew test'
  for(int k = 0; k < split.size(); k+=2) {
    run += ' --tests '
    run += getTestName(split.get(k))
  }
  println(run)
  branches["split${i}"] = {
       sh run
       junit '**/build/test-results/test/*.xml'
       //step([$class: 'JUnitResultArchiver', testResults: 'build/test-results/*.xml'])
  }
}
parallel branches
}
