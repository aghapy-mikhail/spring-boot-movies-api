pipeline {
    agent any

    stages {
       

       stage('Package the API') {
            steps {
                
                    script {
                      sh 'mvn -f Movies/pom.xml package -DskipTests=true'
                       
                    } // script
               
        	} // steps
        } // stage 
        
    //Run the api in docker with it's keycloak and database dependencies
        stage('Run Movies-API') {
          steps {
            sh 'docker-compose  Movies/docker-compose.yml up -d'
          }

        }   

        stage('Unit Testing') {
            steps {
              sh 'mvn -f Movies/pom.xml clean test'
            }        
          
            post {
                success {
                        // junit '**/Movies/target/surefire-reports/*.xml'
                        script {
                            publishHTML (target: [
                                allowMissing: true,
                                alwaysLinkToLastBuild: false,
                                keepAll: true,
                                reportDir: 'Movies/target/site/jacoco', 
                                reportFiles: 'index.html',
                                reportName: "Jacoco Report"
                            ])
                        } 
                     
                } 
            }             
        }

 

        stage('Integration Testing') {
          steps {
            sh 'newman run postman/movies-api.postman_collection.json -e postman/movies-local.postman_environment.json -r cli,html --reporter-html-export Movies/target/site/newman/test-report.html'
          }

          post {
            success {
              script {
                publishHTML(target: [
                  allowMissing: true,
                  alwaysLinkToLastBuild: false,
                  keepAll: true,
                  reportDir: 'Movies/target/site/newman',
                  reportFiles: 'test-report.html',
                  reportName: 'Newman Integration Report'
                ])
              }
            }
          }
        }

        stage('JMeter Load Testing') {
          steps {
            bat '''
             mkdir -p Movies\\target\\site\\jmeter\\report
              jmeter -n -t jmeter/movies-api-x-docker.jmx -l Movies/target/site/jmeter/load-test.csv -e -o Movies/target/site/jmeter/report
            '''
          }

          post {
            success {
              script {
                publishHTML(target: [
                  allowMissing: true,
                  alwaysLinkToLastBuild: false,
                  keepAll: true,
                  reportDir: 'Movies/target/site/jmeter/report',
                  reportFiles: 'index.html',
                  reportName: 'JMeter Report'
                ])
              }
            }
          }
        }    

        stage('Deploy API') {
          steps {
            script {
              withCredentials([usernamePassword(
                credentialsId: 'ms3-nexus',
                usernameVariable: 'USERNAME',
                passwordVariable: 'PASSWORD',
              )]) {
                bat '''
                  docker login docker-public.kube.cloudapps.ms3-inc.com --username=%USERNAME% --password=%PASSWORD%
                  docker build Movies/. -t docker-public.kube.cloudapps.ms3-inc.com/movies-api:latest
                  docker push docker-public.kube.cloudapps.ms3-inc.com/movies-api --all-tags
                '''
              }
            }
          } 

          post {
            always {
              script {
                
                 sh 'docker compose -f Movies/docker-compose.yml down'
                 sh 'docker rmi docker-public.kube.cloudapps.ms3-inc.com/movies-api:latest'
                
              }
            }
          }
        }    

    }
}
