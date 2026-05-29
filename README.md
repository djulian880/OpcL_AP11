# OpcL_AP11
Projet 11 Architecte logiciel : POC

# Workflow Git:
Le principe Gitflow est utilisé: 

La branche main contient les version finalisées

La branche dev contient le développement en cours

Les fonctionnalités nouvelles sont des branches issues de la branche dev.

Les microservices sont stockés chacun dans un répertoire différent du repository.

# Execution des test:
Les tests unitaires et d'intégration sont mis en place pour les micro-services bed-availability, hospital et appointment.
Une base de données H2 en mémoire est utilisée pour l'éxecution des tests. 

# Configuration du pipeline CI/CD
Le pipeline est configuré pour se déclencher sur un push sur la branche main, dev ou les branches dédiées à chaque microservice.
Si un push est fait sur une branche spécifique, seul le micro-service de la branche concerné sera testé et buildé.
Le pipeline s'arrête à la phase de build, aucun déploiement n'est configuré.
