document.getElementById("fruFrom").onchange = function() {
    localStorage.setItem('fruFrom', document.getElementById("fruFrom").value);
    location.reload();
};

if (localStorage.getItem('fruFrom')) {
    document.getElementById("fruFrom").options[localStorage.getItem('fruFrom')].selected = true;
}