-- MySQL dump 10.13  Distrib 8.0.38, for macos14 (arm64)
--
-- Host: localhost    Database: opcl_p11_hospital
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `hospital_specialities`
--

LOCK TABLES `hospital_specialities` WRITE;
/*!40000 ALTER TABLE `hospital_specialities` DISABLE KEYS */;
INSERT INTO `hospital_specialities` VALUES (71,'Pneumologie'),(71,'Médecine interne'),(71,'Qualifié en Médecine générale'),(72,'Spécialiste en Médecine générale'),(72,'Qualifié en Médecine générale'),(72,'Gériatrie'),(73,'Spécialiste en Médecine générale'),(74,'Pédiatrie'),(74,'Gériatrie'),(75,'Spécialiste en Médecine générale'),(75,'Qualifié en Médecine générale'),(76,'Santé publique et Médecine sociale'),(76,'Oncologie, option médicale'),(76,'Médecine vasculaire'),(76,'Gynécologie médicale'),(76,'Rhumatologie'),(76,'Médecine du travail'),(76,'Dermatologie et Vénéréologie'),(76,'Médecine interne'),(76,'Hématologie, option Maladie du sang'),(76,'Gynécologie-obstétrique'),(76,'Ophtalmologie'),(76,'Neuro-chirurgie'),(76,'Médecine physique et de réadaptation'),(76,'Pédiatrie'),(76,'Spécialiste en Médecine générale'),(76,'Médecine d\'urgence'),(76,'Psychiatrie'),(76,'Anesthésie-réanimation'),(76,'Chirurgie plastique reconstructrice et esthétique'),(76,'Anatomie et Cytologie pathologiques'),(76,'Hématologie'),(76,'ORL et Chirurgie cervico-faciale'),(76,'Gynéco-obstétrique et Gynéco-médicale, option Gynéco-obstétrique'),(76,'Chirurgie urologique'),(76,'Hématologie, option Onco-hématologie'),(76,'Médecine légale et expertises médicales'),(76,'Médecine interne et immunologie clinique'),(76,'Médecine générale'),(76,'Allergologie'),(76,'Génétique médicale'),(76,'Maladies infectieuses et tropicales'),(76,'Chirurgie thoracique et cardio-vasculaire'),(76,'Gastro-entérologie et Hépatologie'),(76,'Gériatrie'),(76,'Médecine intensive-réanimation'),(76,'Biologie médicale option hématologie et immunologie'),(76,'Néphrologie'),(76,'Biologie médicale'),(76,'Oncologie, option radiothérapie'),(76,'Médecine nucléaire'),(76,'Radiologie imagerie médicale option radiologie interventionnelle avancée'),(76,'Neurologie'),(76,'Chirurgie vasculaire'),(76,'Radio-diagnostic'),(76,'Pédiatrie option néonatologie'),(76,'Chirurgie générale'),(76,'Chirurgie orthopédique et Traumatologie'),(76,'Qualifié en Médecine générale'),(76,'Cardiologie et Maladies vasculaires'),(76,'Pneumologie'),(76,'Endocrinologie et Métabolisme'),(76,'Chirurgie viscérale et digestive'),(77,'Qualifié en Médecine générale'),(77,'Gériatrie'),(78,'Pédiatrie'),(78,'Chirurgie infantile'),(78,'Anesthésie-réanimation'),(78,'Dermatologie et Vénéréologie'),(78,'Gynécologie-obstétrique'),(79,'Spécialiste en Médecine générale'),(79,'Médecine générale'),(79,'Psychiatrie'),(79,'Anesthésie-réanimation'),(79,'Chirurgie générale'),(79,'Gynécologie-obstétrique'),(79,'Chirurgie orthopédique et Traumatologie'),(79,'Qualifié en Médecine générale'),(80,'Pédiatrie'),(80,'Oncologie, option radiothérapie'),(80,'Spécialiste en Médecine générale'),(80,'Médecine d\'urgence'),(80,'Santé publique et Médecine sociale'),(80,'Neurologie'),(80,'Psychiatrie option enfant et adolescent'),(80,'Gynécologie médicale'),(80,'Psychiatrie'),(80,'Anesthésie-réanimation'),(80,'Radio-diagnostic'),(80,'Gastro-entérologie et Hépatologie'),(80,'Qualifié en Médecine générale'),(80,'Gériatrie'),(80,'Chirurgie orale'),(80,'Médecine intensive-réanimation'),(80,'Chirurgie maxillo-faciale (réforme 2017)'),(80,'Chirurgie urologique'),(80,'Endocrinologie et Métabolisme'),(80,'Médecine du travail'),(80,'Néphrologie'),(80,'Médecine interne'),(80,'Gynécologie-obstétrique'),(80,'Médecine physique et de réadaptation'),(81,'Spécialiste en Médecine générale'),(82,'Pédiatrie'),(82,'Spécialiste en Médecine générale'),(82,'Médecine générale'),(82,'Chirurgie vasculaire'),(82,'Anesthésie-réanimation'),(82,'Radio-diagnostic'),(82,'Chirurgie plastique reconstructrice et esthétique'),(82,'Chirurgie orthopédique et Traumatologie'),(82,'Qualifié en Médecine générale'),(82,'Chirurgie urologique'),(82,'Dermatologie et Vénéréologie'),(82,'Médecine interne'),(82,'Gynécologie-obstétrique'),(83,'Spécialiste en Médecine générale'),(83,'Qualifié en Médecine générale'),(83,'Gériatrie'),(84,'Spécialiste en Médecine générale'),(84,'Ophtalmologie'),(84,'Gériatrie'),(85,'Qualifié en Médecine générale'),(86,'Pédiatrie'),(86,'Spécialiste en Médecine générale'),(86,'Médecine générale'),(86,'Anesthésie-réanimation'),(86,'Médecine interne'),(86,'Gynécologie-obstétrique'),(86,'Chirurgie générale'),(86,'Chirurgie orthopédique et Traumatologie'),(86,'Qualifié en Médecine générale'),(87,'Spécialiste en Médecine générale'),(87,'Pneumologie'),(87,'Dermatologie et Vénéréologie'),(87,'Qualifié en Médecine générale'),(87,'Gériatrie'),(88,'Santé publique et Médecine sociale'),(88,'Oncologie, option médicale'),(88,'Médecine vasculaire'),(88,'Gynécologie médicale'),(88,'Rhumatologie'),(88,'Médecine du travail'),(88,'Hématologie, option Maladie du sang'),(88,'Dermatologie et Vénéréologie'),(88,'Médecine interne'),(88,'Gynécologie-obstétrique'),(88,'Ophtalmologie'),(88,'Neuro-chirurgie'),(88,'Médecine physique et de réadaptation'),(88,'Pédiatrie'),(88,'Spécialiste en Médecine générale'),(88,'Médecine d\'urgence'),(88,'Psychiatrie'),(88,'Anesthésie-réanimation'),(88,'Chirurgie plastique reconstructrice et esthétique'),(88,'Anatomie et Cytologie pathologiques'),(88,'Oto-rhino-laryngologie'),(88,'Hématologie'),(88,'ORL et Chirurgie cervico-faciale'),(88,'Chirurgie urologique'),(88,'Médecine interne et immunologie clinique'),(88,'Allergologie'),(88,'Médecine générale'),(88,'Maladies infectieuses et tropicales'),(88,'Chirurgie thoracique et cardio-vasculaire'),(88,'Gastro-entérologie et Hépatologie'),(88,'Gériatrie'),(88,'Gastro-ent��rologie et Hépatologie'),(88,'Médecine intensive-réanimation'),(88,'Néphrologie'),(88,'Biologie médicale'),(88,'Oncologie, option radiothérapie'),(88,'Médecine nucléaire'),(88,'Radiologie imagerie médicale option radiologie interventionnelle avancée'),(88,'Neurologie'),(88,'Chirurgie vasculaire'),(88,'Chirurgie infantile'),(88,'Radio-diagnostic'),(88,'Chirurgie orthopédique et Traumatologie'),(88,'Chirurgie générale'),(88,'Qualifié en Médecine générale'),(88,'Cardiologie et Maladies vasculaires'),(88,'Endocrinologie, diabétologie, nutrition'),(88,'Pneumologie'),(88,'Endocrinologie et Métabolisme'),(88,'Chirurgie viscérale et digestive'),(88,'Psychiatrie option psychiatrie personne âgée');
/*!40000 ALTER TABLE `hospital_specialities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `hospitalbdd`
--

LOCK TABLES `hospitalbdd` WRITE;
/*!40000 ALTER TABLE `hospitalbdd` DISABLE KEYS */;
INSERT INTO `hospitalbdd` VALUES (71,'6 R DU MOULIN MUNSTER','HOPITAL LOEWEL DE MUNSTER'),(72,'5 R DU DR MANGENEY MULHOUSE','MAISON MEDICALE POUR PERSONNES AGEES'),(73,'2A 2 R DU JURA MULHOUSE','HOP JOUR MULHOUSE 68 G06 GHRMSA'),(74,'17 R JEAN JACQUES BOCK STE MARIE AUX MINES','HOPITAL INTERCOMMUNAL DU VAL D\'ARGENT'),(75,'5 R SAINT DAMIEN ST LOUIS','POLE PUBLIC SAINT-LOUIS - GHRMSA'),(76,'20 R DU DR LAENNEC MULHOUSE','HOPITAL EMILE MULLER'),(77,'7 R GEORGES RISLER CERNAY','HOPITAL DE CERNAY'),(78,'46 R DU STAUFFEN COLMAR','CENTRE MEDICAL LE PARC'),(79,'1 R SAINT JACQUES THANN','HOPITAL DE THANN'),(80,'87 AV D\'ALTKIRCH MULHOUSE','HOPITAL DU HASENRAIN'),(81,'7 R COLBERT ENSISHEIM','HOP INTERCOM ENSISHEIM NEUF-BRISACH'),(82,'2 R JEAN SCHLUMBERGER GUEBWILLER','CENTRE HOSPITALIER DE GUEBWILLER'),(83,'122 R DU LOGELBACH COLMAR','CENTRE POUR PERSONNES AGEES'),(84,'40 R DU STAUFFEN COLMAR','CENTRE DEPART. DE REPOS ET DE SOINS'),(85,'13 R DU CHATEAU RIBEAUVILLE','HOPITAL DE RIBEAUVILLE'),(86,'23 R DU TROISIEME ZOUAVE ALTKIRCH','CENTRE HOSPITALIER D\'ALTKIRCH'),(87,'1 R HENRI HAEFFELY PFASTATT','CENTRE HOSPITALIER DE PFASTATT'),(88,'39 AV DE LA LIBERTE COLMAR','HOPITAL LOUIS PASTEUR');
/*!40000 ALTER TABLE `hospitalbdd` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-26 18:13:35
