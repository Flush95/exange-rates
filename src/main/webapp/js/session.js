
document.getElementById("fru").onchange = function() {
    localStorage.setItem('fru', document.getElementById("fru").value);
    location.reload();
}

if (localStorage.getItem('fru')) {
    document.getElementById("fru").options[localStorage.getItem('fru')].selected = true;
}
