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
              }
          }
      }
    stage('Test') {
        steps {
            PrintStage()
            needRunParallelTest()
            //sh './gradlew test'
            //junit '**/build/test-results/test/*.xml'
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
def splits = splitTests parallelism: count(4), generateInclusions: true, estimateTestsFromFiles: true
def branches = [:]
for (int i = 0; i < splits.size(); i++) {
  def split = splits.get(i);
  println(split)  
//   def run = './gradlew test'
//   for(int k = 0; k < split.list.size(); k++) {
//     run += ' --tests '
//     run += split.list.get(k)
//   }
//   println(run)
  branches["split${i}"] = {
      //writeFile file: 'exclusions.txt', text: exclusions.join("\n")//split.list.join
      
//       println(split)
//       println('-----------------------')
//       sh run
      
//       if(split.includes){
//         //
//       } else {
//         //
//       }
      
      //sh "./gradlew -I ./exclusions.gradle clean check"
      //sh "./gradlew -Dsurefire.excludesFile=exclusions.txt"
      //step([$class: 'JUnitResultArchiver', testResults: 'build/test-results/*.xml'])//./gradlew test --tests "com.xyz.b.module.TestClass.testToRun"
  }
}
parallel branches
}

//         if (split.includes) {
//           writeFile file: "build/parallel-test-includes-${i}.txt", text: split.list.join("\n")
//           run += " -Dsurefire.includesFile=build/parallel-test-includes-${i}.txt"
//         } else {
//           writeFile file: "build/parallel-test-excludes-${i}.txt", text: split.list.join("\n")
//           run += " -Dsurefire.excludesFile=build/parallel-test-excludes-${i}.txt"
//         }
