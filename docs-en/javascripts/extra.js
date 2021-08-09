const LANG_KEY = "cpms-tabbed-lang";
// Array of tab names selected by user
let tabbedLanguagePrefs;

try {
    const rawPref = localStorage.getItem(LANG_KEY);
    tabbedLanguagePrefs = rawPref ? JSON.parse(rawPref) : [];
} catch(e) {
    tabbedLanguagePrefs = [];
    console.error(e);
}

function applyOperationToTabGroup(operation) {
    Array.from(document.querySelectorAll(".tabbed-set")).forEach(operation);
}

function getTabLanguage(tab) {
    return tab.labels.length ? tab.labels[0].innerText.toLowerCase() : null;
}

function setLanguageForTabs() {
    applyOperationToTabGroup(function(tabGroup) {
        const tabs = tabGroup.querySelectorAll("input");
        const tabLanguages = Array.from(tabs).map(getTabLanguage).filter(lang => lang !== null);
        const preferredLanguage = tabbedLanguagePrefs.find((lang) => tabLanguages.indexOf(lang) !== -1);
        if(preferredLanguage) {
            tabs.forEach((tab) => {
                const tabLanguage = getTabLanguage(tab);
                tab.checked = tabLanguage === preferredLanguage;
            });
        }
    });
}

// Listen for code tab changes
applyOperationToTabGroup(function(tabGroup) {
    const tabs = tabGroup.querySelectorAll("input");
    Array.from(tabs).forEach((tab) => {
        tab.onclick = function() {
            // Change all the code tabs to the same language when one is changed.
            var language = getTabLanguage(tab);
            const languageIndex = tabbedLanguagePrefs.indexOf(language);
            if(languageIndex !== -1) {
                tabbedLanguagePrefs.splice(languageIndex, 1);
            }
            tabbedLanguagePrefs.unshift(language);
            localStorage.setItem(LANG_KEY, JSON.stringify(tabbedLanguagePrefs));
            setLanguageForTabs();
        }
    });
});

// Apply language preference from URL
if (tabbedLanguagePrefs.length) {
    setLanguageForTabs();
}

// Set blank targets for external reference links
let tags = ["android", "ios", "mbt", "mtb", "cards", "mdk", "admin", "api"];
let externalPaths = [
    /[^.]*\.sap\.com(?!\/doc\/f53c64b93e5140918d676b927a3cd65b)/g,
    ...tags.map(tag => new RegExp("reference\\/" + tag))
];

Array.from(document.querySelectorAll("a"))
    .filter(node => externalPaths.find(regex => regex.test(node.href)))
    .forEach(node => node.target = "_blank")

// Search query URL
var search = new URLSearchParams(window.location.search);
const query = search.get("q");
if(query) {
    const queryComponent = document.querySelector("[data-md-component=\"search-query\"]");
    queryComponent.value = query;
    queryComponent.focus();
}
