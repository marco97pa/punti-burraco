var strings={
    appName:{
        'en':'Points Buraco',
        'it':'Punti Burraco',
    },
    shortDesc:{
        'en':'More than a simple buraco scorekeeper',
        'it':'Più di un semplice segnapunti per le tue partite di burraco',
    },
    devRole:{
        'en': 'Developer',
        'it': 'Sviluppatore',
    },
    devDesc:{
        'en': 'years, made in Sicily, Italy',
        'it': 'anni, Sicilia',
    },
    devBtnInfo:{
        'en': 'More info',
        'it': 'Scopri di più',
    },
    appFeature1:{
        'en': 'Automated culculations',
        'it': 'Calcoli automatici',
    },
    appFeature1Desc:{
        'en': 'Better than the classic sheet of paper: points are added automatically',
        'it': 'Molto meglio della classica carta e penna: i punti vengono sommmati in automatico',
    },
    appFeature2:{
        'en': 'Share your results',
        'it': 'Condividi i tuoi risultati',
    },
    appFeature2Desc:{
        'en': 'Boast yourself on Facebook, Instagram, Whatsapp and other social networks',
        'it': 'Vantati delle tue vittorie su Facebook, Instagram, Whatsapp e altre app',
    },
    appFeature3:{
        'en': '2, 3 or 4 players',
        'it': '2, 3 or 4 players',
    },
    appFeature3Desc:{
        'en': 'Play with as many friends as you want',
        'it': 'Gioca con tutti gli amici che vuoi',
    },
    appFeature4:{
        'en': 'Customize',
        'it': 'Personalizza',
    },
    appFeature4Desc:{
        'en': 'Choose your favourite theme, your profile pic, your insert method and more in the app',
        'it': 'Scegli il tuo tema preferito, la tua immagine di profilo, imposta il tuo metodo d\'inserimento e altro nell\'app',
    },
    appFeature5:{
        'en': 'Invite to your match',
        'it': 'Invita alla tua partita',
    },
    appFeature5Desc:{
        'en': 'Anyone can join your match and see his score right on his device',
        'it': 'Chiunque può unirsi alla tua partita e vedere il proprio punteggio sul suo smartphone',
    },
    appFeature6:{
        'en': 'Open source',
        'it': 'Open source',
    },
    appFeature6Desc:{
        'en': 'The app is safe, as the code is available to anyone on',
        'it': 'L\'app è sicura, il suo codice è pubblicamente disponibile su',
    },
    features:{
        'en': 'Features',
        'it': 'Funzionalità',
    },
    featuresDesc:{
        'en': 'This is what makes Points Buraco one of the best buraco scorekeeper on the Google Play Store',
        'it': 'Questo è ciò che rende Punti Burraco uno dei migliori segnapunti di burraco sul Google Play Store',
    },
    team:{
        'en': 'Development',
        'it': 'Sviluppo',
    },
    teamDesc:{
        'en': 'Who is behind this awesome project?',
        'it': 'Chi si cela dietro questo fantastico progetto?',
    },
}

function localize(){
    var locale = "en";
    var userLang = navigator.language || navigator.userLanguage; 
    switch(userLang){
        case "it-IT":
        case "it":
            locale = "it";
        break;
        default:
            locale = "en";
    }
    //Set all strings
    document.getElementById("app_name").innerHTML = strings['appName'][locale];
    document.getElementById("app_desc").innerHTML = strings['shortDesc'][locale];
    document.getElementById("recap_app_name").innerHTML = strings['appName'][locale];
    document.getElementById("recap_short_desc").innerHTML = strings['shortDesc'][locale];
    document.getElementById("dev_role").innerHTML = strings['devRole'][locale];
    document.getElementById("dev_desc").innerHTML = age() + " " + strings['devDesc'][locale];
    document.getElementById("dev_info").innerHTML = strings['devBtnInfo'][locale];
    document.getElementById("team").innerHTML = strings['team'][locale];
    document.getElementById("team_desc").innerHTML = strings['teamDesc'][locale];
    document.getElementById("features").innerHTML = strings['features'][locale];
    document.getElementById("features_desc").innerHTML = strings['featuresDesc'][locale];
    document.getElementById("feature_1").innerHTML = strings['appFeature1'][locale];
    document.getElementById("feature_2").innerHTML = strings['appFeature2'][locale];
    document.getElementById("feature_3").innerHTML = strings['appFeature3'][locale];
    document.getElementById("feature_4").innerHTML = strings['appFeature4'][locale];
    document.getElementById("feature_5").innerHTML = strings['appFeature5'][locale];
    document.getElementById("feature_6").innerHTML = strings['appFeature6'][locale];
    document.getElementById("feature_1_desc").innerHTML = strings['appFeature1Desc'][locale];
    document.getElementById("feature_2_desc").innerHTML = strings['appFeature2Desc'][locale];
    document.getElementById("feature_3_desc").innerHTML = strings['appFeature3Desc'][locale];
    document.getElementById("feature_4_desc").innerHTML = strings['appFeature4Desc'][locale];
    document.getElementById("feature_5_desc").innerHTML = strings['appFeature5Desc'][locale];
    document.getElementById("feature_6_desc").innerHTML = strings['appFeature6Desc'][locale] + document.getElementById("feature_6_desc").innerHTML;
}

function age(){
    var nascita = moment("19971124", "YYYYMMDD");
    var anni = moment().diff(nascita, 'years', true);
    anni = parseInt(anni);
    return anni;
}

// Your web app's Firebase configuration
var firebaseConfig = {
    apiKey: "AIzaSyADfNMKUfLBuHi9DQABVBaQX9Oki7BKDfg",
    authDomain: "points-buraco.firebaseapp.com",
    databaseURL: "https://points-buraco.firebaseio.com",
    projectId: "points-buraco",
    storageBucket: "points-buraco.appspot.com",
    messagingSenderId: "9826407298",
    appId: "1:9826407298:web:1ec1f6104e03bdb078359b",
    measurementId: "G-HTW51V6DVD"
  };
  // Initialize Firebase
  firebase.initializeApp(firebaseConfig);
  firebase.analytics();

  const remoteConfig = firebase.remoteConfig();
  remoteConfig.defaultConfig = ({
    'downloads': 10000,
  });
  remoteConfig.fetchAndActivate()
  .then(activated => {
    console.log("Activated?", activated);
    document.getElementById("downloads").innerHTML = "+" + remoteConfig.getNumber('downloads') + " downloads";
  })
  .catch((err) => {
    console.error(err);
  });