package com.ipiecoles.java.java350.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import com.ipiecoles.java.java350.service.EmployeService;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {
	
	@InjectMocks
	private EmployeService employeService;
	
	@Mock
	private EmployeRepository employeRep;
	
	
	@Test
	public void testEmbaucheEmployeCommercialPleinTempsBTS() throws EmployeException {
		
		//Given
		String nom = "Doe";
		String prenom = "John";
		Poste poste = Poste.COMMERCIAL;
		NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
		Double tempsPartiel = 1.0;
		//findLastMatricule renvoie null
		Mockito.when(employeRep.findLastMatricule()).thenReturn("00345");
		//findByMatricule renvoie null
		Mockito.when(employeRep.findByMatricule("C00346")).thenReturn(null);
		
		//When
		employeService.embaucheEmploye(nom,prenom,poste,niveauEtude,tempsPartiel);
		
		//Then
		//Vérification en BDD si l'employe est bien créé (nom, prenom, matricule, salaire, performance et temps partiel)
		ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
		Mockito.verify(employeRep, Mockito.times(1)).save(employeArgumentCaptor.capture());
		Employe employe = employeArgumentCaptor.getValue();
		
		Assertions.assertThat(employe.getNom()).isEqualTo(nom);
		Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
		Assertions.assertThat(employe.getMatricule()).isEqualTo("C00346");
		Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
		Assertions.assertThat(employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
		.isEqualTo(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(tempsPartiel);
		Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
		Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
		//1521.22*1.2*1.0
		Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);
		
		
	}
	
	
	
	@Test
	public void testEmbaucheEmployeLimiteMatricule() throws EmployeException {
		
		//Given
		String nom = "Doe";
		String prenom = "John";
		Poste poste = Poste.COMMERCIAL;
		NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
		Double tempsPartiel = 1.0;
		//findLastMatricule renvoie null
		Mockito.when(employeRep.findLastMatricule()).thenReturn("99999");
		
		//When/Then AssertJ
		//Cette technique fait la même chose qu'en dessous
		Assertions.assertThatThrownBy(() -> {employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);})
		.isInstanceOf(EmployeException.class).hasMessage("Limite des 100000 matricules atteinte !");
		
		//When
		try {
			employeService.embaucheEmploye(nom,prenom,poste,niveauEtude,tempsPartiel);
			Assertions.fail("Aurait du planter !");
		} catch (Exception e){
			//Then
			Assertions.assertThat(e).isInstanceOf(EmployeException.class);
			Assertions.assertThat(e.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
		}
		
		
		
	}
	
}

