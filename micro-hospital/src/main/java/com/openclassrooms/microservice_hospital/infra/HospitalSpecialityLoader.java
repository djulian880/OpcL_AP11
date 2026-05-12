package com.openclassrooms.microservice_hospital.infra;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.parser.*;
import com.openclassrooms.microservice_hospital.infra.entity.Bed;
import com.openclassrooms.microservice_hospital.infra.entity.Hospital;
import com.openclassrooms.microservice_hospital.infra.entity.Speciality;
import com.openclassrooms.microservice_hospital.infra.repository.BedRepository;
import com.openclassrooms.microservice_hospital.infra.repository.HospitalRepository;
import com.openclassrooms.microservice_hospital.infra.repository.SpecialityRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class HospitalSpecialityLoader {

    @Autowired
    HospitalRepository hospitalRepository;

    @Autowired
    SpecialityRepository specialityRepository;

    @Autowired
    BedRepository bedRepository;

    IGenericClient client;


    public HospitalSpecialityLoader(){
        FhirContext ctx = FhirContext.forR4();
        client = ctx.newRestfulGenericClient("https://gateway.api.esante.gouv.fr/fhir");

        // Ajout de la clé API à chaque requête
        client.registerInterceptor(new IClientInterceptor() {
            @Override
            public void interceptRequest(IHttpRequest request) {
                request.addHeader("ESANTE-API-KEY", "2637adef-a6d3-4852-b7ad-efa6d34852dc");
            }
            @Override
            public void interceptResponse(IHttpResponse response) {}
        });
    }

    @PostConstruct
    @Transactional
    public void init() {
        try {
            if(specialityRepository.count() == 0) {
                loadSpecialitiesFromFHIR();

            }
            if(hospitalRepository.count() == 0) {
                loadAllHospitals();
            }
            if(bedRepository.count() == 0) {
                loadNumberOfBeds();
            }


        } catch (Exception e) {
            System.err.println("Erreur chargement spécialités : " + e.getMessage());
        }
    }



    public void loadNumberOfBeds()  {
        Random random = new Random();
        List<Hospital> hospitalList = hospitalRepository.findAll();
        for(Hospital hospital : hospitalList) {
            for(Speciality speciality : hospital.getSpecialities()) {
                Bed bed = new Bed();
                bed.setHospital(hospital);
                bed.setSpeciality(speciality);
                bed.setTotalNumberOfBeds(random.nextInt(0,30)+10);
                bedRepository.save(bed);
            }
        }
    }


    public void loadAllHospitals() {


        List<Organization> listOrganization=new ArrayList<>();

        // Rechercher les établissements hospitaliers par département
        Bundle bundle = client.search()
                .forResource(org.hl7.fhir.r4.model.Organization.class)
                .where(org.hl7.fhir.r4.model.Organization.TYPE.exactly().code("355")) // Centre Hospitalier
                .and(org.hl7.fhir.r4.model.Organization.ADDRESS_POSTALCODE.matches().value("68"))  // Bas-Rhin
                .and(org.hl7.fhir.r4.model.Organization.ACTIVE.exactly().code("true"))
                .count(20)
                .returnBundle(Bundle.class)
                .execute();

        String idHospital="";
        int count = 0;
        // Boucle sur toutes les pages
        while (bundle != null) {
            //hospitalRepository.deleteAll();
            // Extraire les résultats de la page courante
            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                org.hl7.fhir.r4.model.Organization org = (org.hl7.fhir.r4.model.Organization) entry.getResource();
                Organization organization = new Organization();

                //System.out.println("Nom       : " + org.getName());
                String adresse = "";
                String rue = org.getAddressFirstRep().getLine().getFirst().getValue();
                if (rue.contains("BP")) {
                    rue = rue.substring(0, rue.indexOf("BP") - 1);
                }

               /* getExtension().get(0)+" "+org.getAddressFirstRep().getExtension().get(1)+
                        org.getAddressFirstRep().getExtension().get(2);*/
                String ville = org.getAddressFirstRep().getCity();
                if (ville.contains("CEDEX")) {
                    ville = ville.substring(0, ville.indexOf("CEDEX") - 1);
                }

                adresse = rue + " " + ville;
                //System.out.println("Adresse   : " + org.getAddressFirstRep().getCity()+" "+org.getAddressFirstRep().getPostalCode()+" "+org.getAddressFirstRep().getLine());
                //System.out.println("Adresse   : " + adresse);
                idHospital = org.getIdElement().getIdPart();
                //System.out.println("Id  : " + idHospital);
                organization.setName(org.getName());
                organization.setId(idHospital);
                organization.setAdress(adresse);
                listOrganization.add(organization);
                //count++;
            }
            // Vérifier s'il existe une page suivante
            if (bundle.getLink(Bundle.LINK_NEXT) != null) {
                bundle = client.loadPage()
                        .next(bundle)
                        .execute();
            } else {
                bundle = null; // fin de la pagination
            }
        }

        for(Organization organization : listOrganization){
            ArrayList<String> listSpecialties=retrieveSpecialtiesFromOrganization(organization);
            if(listSpecialties.size()>0){
                Hospital hospital = new Hospital();
                hospital.setAddress(organization.getAdress());
                hospital.setName(organization.getName());

                Set<Speciality> specialities=new HashSet<>();
                for(String speciality : listSpecialties){

                    Optional<Speciality> optSpecialityEntity = specialityRepository.findByName(speciality);
                    if(optSpecialityEntity.isPresent()){
                        Speciality specialityEntity = optSpecialityEntity.get();
                        specialities.add(specialityEntity);
                    }

                }
                hospital.setSpecialities(specialities);
                System.out.println(hospital.toString());
                hospitalRepository.save(hospital);
            }

        }
        System.out.println("Terminé ");

    }

    public ArrayList<String> retrieveSpecialtiesFromOrganization(Organization organization){
        //List<String> result;
        Set<String> set = new HashSet<String>(); // List → Set (supprime les doublons)


        //List<String> listePraticiens=new ArrayList<String>();
        //String idPraticient="";
        //String idTotal="";
        // Utilisation directe dans la requête suivante
        Bundle praticiensRole = client.search()
                .forResource(PractitionerRole.class)
                .where(PractitionerRole.ORGANIZATION.hasId(organization.getId()))
                .include(PractitionerRole.INCLUDE_PRACTITIONER)
                .count(10)
                .returnBundle(Bundle.class)
                .execute();

        while (praticiensRole != null) {
            // Extraire les résultats de la page courante
            for (Bundle.BundleEntryComponent entry : praticiensRole.getEntry()) {
                try{
                    PractitionerRole practitionerRole = (PractitionerRole) entry.getResource();
                    //System.out.println("****** Début ******: ");
                    String idpractitionerRole=practitionerRole.getIdPart();
                    //System.out.println("idpractitionerRole: " + idpractitionerRole);
                    String idRole=idpractitionerRole.substring(4);
                    idRole=idRole.substring(0,idRole.indexOf("-"));
                    //System.out.println("idRole: " + idRole);

                    String idPractionner=practitionerRole.getPractitioner().getReferenceElement().getIdPart();
                    //System.out.println("getIdPart  : " + idPraticient);
                    //System.out.println("ID complet  : " + idPraticient+"-"+idRole);
                    String idTotal=idPractionner+"-"+idRole;

                    String specialty=retrieveSpecialtyFromPractictionner(idTotal);
                    if(!specialty.isEmpty()){
                        set.add(retrieveSpecialtyFromPractictionner(idTotal));
                    }

                    //listePraticiens.add(idTotal);
                }
                catch(Exception e){

                }

            }
            // Vérifier s'il existe une page suivante
            if (praticiensRole.getLink(Bundle.LINK_NEXT) != null) {
                praticiensRole = client.loadPage()
                        .next(praticiensRole)
                        .execute();
            } else {
                praticiensRole = null; // fin de la pagination
            }


        }

        //List<String> result = new ArrayList<>(set);
        return new ArrayList<>(set);

    }



    public String retrieveSpecialtyFromPractictionner(String idPractionner ){
        //List<String> result=new ArrayList<>();
        //Set<String> set = new HashSet<String>(); // List → Set (supprime les doublons)

        String result="";


        //for(String idpersonne : listePraticiens) {
            Practitioner practitioner = client.read()
                    .resource(Practitioner.class)
                    .withUrl("https://gateway.api.esante.gouv.fr/fhir/v2/Practitioner/"+idPractionner)
                    .execute();

            for (Practitioner.PractitionerQualificationComponent qualif : practitioner.getQualification()) {

                for (Coding code : qualif.getCode().getCoding()) {
                    if(code.getSystem().equals("https://mos.esante.gouv.fr/NOS/TRE_R38-SpecialiteOrdinale/FHIR/TRE-R38-SpecialiteOrdinale")){
                        String spe=code.getDisplay();
                        result=spe.substring(0,spe.indexOf(" (SM)"));
                        /*System.out.println("Système  : " + code.getSystem());
                        System.out.println("Code     : " + code.getCode());
                        System.out.println("Libellé  : " + spe);
                        System.out.println("---");*/
                        //result=spe;
                    }

                }

            }
        //}

       // List<String> result = new ArrayList<>(set);
        return result;
    }


    public void loadSpecialitiesFromFHIR() throws Exception {

        // 1. Télécharger le fichier XML
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://mos.esante.gouv.fr/NOS/TRE_R38-SpecialiteOrdinale/FHIR/TRE-R38-SpecialiteOrdinale/TRE_R38-SpecialiteOrdinale-FHIR.xml"))
                .header("Accept", "application/xml")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            hospitalRepository.deleteAll();
            specialityRepository.deleteAll();
            // 2. Parser le XML avec HAPI FHIR
            FhirContext ctx = FhirContext.forR4();
            IParser parser = ctx.newXmlParser();
            CodeSystem codeSystem = parser.parseResource(CodeSystem.class, response.body());

            // 3. Extraire les concepts (spécialités)
            List<Speciality> specialities = codeSystem.getConcept().stream()
                    .filter(concept -> concept.getDisplay() != null)
                    .map(concept -> {
                        Speciality entity = new Speciality();
                        entity.setCode(concept.getCode());       // ex: "SM06"
                        String display=concept.getDisplay();

                        entity.setName(display.substring(0,display.indexOf(" (")));    // ex: "Oncologie"
                        return entity;
                    })
                    .collect(Collectors.toList());

            // 4. Sauvegarder en base (uniquement si pas déjà présent)
            specialities.forEach(speciality ->
                    specialityRepository.findByName(speciality.getName())
                            .orElseGet(() -> specialityRepository.save(speciality))
            );

            System.out.println(specialities.size() + " spécialités chargées.");

        }

    }


    @Getter
    @Setter
    public class Organization {
        private String adress;
        private String id;
        private String name;
    }



}
