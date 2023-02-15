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
//
//       }
    stage('Test') {
        steps {
            sh './gradlew test'
            junit '**/build/test-results/test/*.xml'
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
