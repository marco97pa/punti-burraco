//DARK THEME
if(getAllUrlParams().dark) {
    document.getElementById('stylesheet').href='css/bottom-nav-dark.css';
   }
   //JUMP to BUG section
   if(getAllUrlParams().bug) {
    show('bug');
   }

    var nascita = moment("19971124", "YYYYMMDD");
    var anni = moment().diff(nascita, 'years', true);
    anni = parseInt(anni);
    document.getElementById("anni").innerHTML = anni;

    var now = moment().format("YYYY");
    document.getElementById("year").innerHTML = now;

    function getAndroidVersion(ua) {
        ua = (ua || navigator.userAgent).toLowerCase();
        var match = ua.match(/android\s([0-9\.]*)/);
        return match ? match[1] : false;
    }

    if(getAndroidVersion()!=false){
       document.getElementById("android").value = "Android "+getAndroidVersion();
    }


    ///URL EXTRACTOR
    function getAllUrlParams(url) {

     // get query string from url (optional) or window
     var queryString = url ? url.split('?')[1] : window.location.search.slice(1);

     // we'll store the parameters here
     var obj = {};

     // if query string exists
     if (queryString) {

       // stuff after # is not part of query string, so get rid of it
       queryString = queryString.split('#')[0];

       // split our query string into its component parts
       var arr = queryString.split('&');

       for (var i = 0; i < arr.length; i++) {
         // separate the keys and the values
         var a = arr[i].split('=');

         // set parameter name and value (use 'true' if empty)
         var paramName = a[0];
         var paramValue = typeof (a[1]) === 'undefined' ? true : a[1];

         // (optional) keep case consistent
         paramName = paramName.toLowerCase();
         if (typeof paramValue === 'string') paramValue = paramValue.toLowerCase();

         // if the paramName ends with square brackets, e.g. colors[] or colors[2]
         if (paramName.match(/\[(\d+)?\]$/)) {

           // create key if it doesn't exist
           var key = paramName.replace(/\[(\d+)?\]/, '');
           if (!obj[key]) obj[key] = [];

           // if it's an indexed array e.g. colors[2]
           if (paramName.match(/\[\d+\]$/)) {
             // get the index value and add the entry at the appropriate position
             var index = /\[(\d+)\]/.exec(paramName)[1];
             obj[key][index] = paramValue;
           } else {
             // otherwise add the value to the end of the array
             obj[key].push(paramValue);
           }
         } else {
           // we're dealing with a string
           if (!obj[paramName]) {
             // if it doesn't exist, create property
             obj[paramName] = paramValue;
           } else if (obj[paramName] && typeof obj[paramName] === 'string'){
             // if property does exist and it's a string, convert it to an array
             obj[paramName] = [obj[paramName]];
             obj[paramName].push(paramValue);
           } else {
             // otherwise add the property
             obj[paramName].push(paramValue);
           }
         }
       }
     }

     return obj;
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
       document.getElementById("downloads").innerHTML = remoteConfig.getNumber('downloads');
     })
     .catch((err) => {
       console.error(err);
     });