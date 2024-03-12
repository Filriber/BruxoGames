const formulario = document.querySelector("form")

const Inome = document.querySelector(".nome")
const Iemail = document.querySelector(".email")
const Icpf = document.querySelector(".cpf")
const Isenha = document.querySelector(".senha")
const Isenhaconf = document.querySelector(".senhaconf")
const Igrupo = document.querySelector(".grupo")



function cadastrar(){
    
    fetch("http://localhost:8080/cadastro",
{
    headers:{
        'Accept': 'aplication/json',
        'Content-type': 'aplication/json'
    },
    method: "POST",
    body:   JSON.stringify({
        nome: Inome.value,
        email: Iemail.value,
        senha: Isenha.value,
        cpf: Icpf.value,
        grupo: Igrupo.value

        })
    })

    .then(function(res) {console.log(res)})
    .catch(function(res){console.log(res)})
}

    function limpar (){
        Inome.value = "";
        Iemail.value= "";
        Isenha.value= "";
        Icpf.value= "";
        Igrupo.value= "";

    };

    formulario.addEventListener('submit', function(event){
    event.preventDefault();

    const dados = {
        nome: Inome.value,
        email: Iemail.value,
        cpf: Icpf.value,
        senha: Isenha.value,
        senhaconf: Isenhaconf.value,

    };
    cadastrar();
    limpar();

});