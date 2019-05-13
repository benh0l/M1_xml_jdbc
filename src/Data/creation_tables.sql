create table Joueur (
	numJoueur varchar(11),
	prenom_joueur varchar(50),
	nom_joueur varchar(50),
	num_poste varchar(11),
	equipe varchar(4),
	primary key (numJoueur),
	foreign key (num_poste) references Poste(numero),
	foreign key (equipe) references Equipe(codeEquipe));
	
create table Poste (
	numero varchar(11),
	libelle varchar(50),
	primary key (numero));
	
create table Equipe (
	codeEquipe varchar(4),
	pays varchar(50),
	couleur varchar(50),
	entraineur varchar(50),
	primary key (codeEquipe));
	
create table Arbitre (
	numArbitre varchar(11),
	nom_arbitre varchar(50),
	nationalite varchar(30),
	primary key (numArbitre));
	
create table Stade (
	numStade varchar(11),
	ville varchar(50),
	nomStade varchar(30),
	capacite number(11),
	primary key (numStade));
	
create table Matchs (
	numMatch varchar(11), 
	dateMatch date,
	nbSpect varchar(11),
	numStade varchar(11),
	codeEquipe_r varchar(4),
	score_r varchar(11),
	nbEssais_r varchar(11),
	codeEquipe_d varchar(4),
	score_d varchar(11),
	nbEssais_d varchar(11),
	primary key (numMatch),
	foreign key (numStade) references Stade(numStade),
	foreign key (codeEquipe_r) references Equipe(codeEquipe),
	foreign key (codeEquipe_d) references Equipe(codeEquipe));
	
create table Aribtrer (
	numMatch varchar(11),
	numArbitre varchar(11),
	primary key (numMatch,numArbitre),
	foreign key (numMatch) references Matchs(numMatch),
	foreign key (numArbitre) references Arbitre(numArbitre));
	
create table Jouer (
	numMatch varchar(11),
	numJoueur varchar(11),
	titulaire varchar(2),
	tpsJeu varchar(11),
	nbPoints varchar(11),
	nbEssais varchar(11),
	primary key (numMatch,numJoueur),
	foreign key (numMatch) references Matchs(numMatch),
	foreign key (numJoueur) references Joueur(numJoueur));
