# Using Applitools with the Crossbrowsertesting Jenkins Plugin

Below is an example of how to properly nest the Applitools and CBT plugins in your pipeline script. Be sure to specify the node name (if any) to run the build on, the workspace, your Applitools key, the address to your repository, and the app/test names for Applitools. As all CI processes are different, you can always [contact us](mailto:support@crossbrowsertesting.com) if you need help integrating our service into your build!


```
pipeline {
  agent {
    node {
      label 'NODE-LABEL'
      customWorkspace '/PATH/TO/BUILD/DIRECTORY/'
    }
  }
  environment {
    APPLITOOLS_KEY = 'YOUR_APPLITOOLS_KEY'
  }
  stages {
    stage('CBT') {
      steps { 
        Applitools('https://eyes.applitools.com'){
          cbt(credentialsId: 'cbt', useTestResults: true, useLocalTunnel: false, localTunnelPath: null, tunnelName: null) {
            sh 'git clone ADDRESS_FOR_REPO'
            sh 'mvn test -f PATH/TO/pom.xml -P parallel -DAPP_NAME=CBT -DTEST_NAME=DESIRED_TEST_NAME'
          }
        }
      }
    }
  }
  post {
    always {
      deleteDir()
    }
  }
}
```