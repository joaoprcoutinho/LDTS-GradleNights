## FEUP-LDTS-PROJ
Este trabalho realizado por João Pedro Rodrigues Coutinho (up202108787), Joaquim Afonso Marques da Cunha (up202108779) e Miguel Jorge Medeiros Garrido (up202108889) tem como objetivo a realização do projeto da disciplina "Laboratório de Desenho e Teste de Software" (L.EIC014) onde nos foi proposto a realização de um text-based game em Java. 
Para tal tivemos como ideia um jogo do género RPG (role-playing game) onde o personagem tem de explorar e coletar "gold" para se fortalecer e enfrentar diversos oponentes e desafios durante a sua jornada.

Exemplos jogo:
 ![image](https://user-images.githubusercontent.com/93836408/209411611-fc8738e9-8942-419a-ab5d-552bc881a334.png)
 ![image](https://user-images.githubusercontent.com/93836408/209411627-0ee091de-e26d-484f-833d-d53ad4ca5db9.png)
 ![image](https://user-images.githubusercontent.com/93836408/209411635-493f5597-06f6-4ea3-a5e0-ea1d7c5630cf.png)
 ![image](https://user-images.githubusercontent.com/93836408/209411643-24066da8-7ea8-4759-b67a-a64057574edf.png) 

### IMPLEMENTED FEATURES
- **fight** - O player defronta um inimigo e consoante o resultado este morre ou ganha uma recompensa deixada pelo adversário além de alguma perda de pontos de vida;  
- **loot** - O player recolhe a recompensa do inimigo em caso de vitoria;  
- **shop** - O player tem a possibilidade de utilizar o seu gold em troca de items que o fortalecem;
- **set strategy** - Consoante a strategy determinada por um cálculo com um número _Random_ a shop aumenta ou não os preços dos items
- **AsciiReader** - Permite a leitura de um ficheiro .txt com o objetivo de utilizar como imagem no terminal
- **use** - Um player consegue usar um item que se encontre no seu inventário

```java
public int buyItem(Player player, String itemName, boolean dup) {
        for(Item item: items) {
            if(item.getName().equals(itemName) &&
                    ((player.getGold() >= item.getValue() && !dup) ||
                            (player.getGold() >= item.getValue()*2 && dup))) {
                player.addItem(item);
                int spent = -1;
                if(dup) {
                    spent = item.getValue() * 2;
                    player.setGold(player.getGold() - spent);
                }
                else {
                    spent = item.getValue();
                    player.setGold(player.getGold() - spent);
                }
                items.remove(item);
                return spent;
            }
        }
        return -1;
    }
```

```java
    public int resultFight() {
        while (player.isAlive() && enemy.isAlive()) {
            player.setHP(player.getHP() - enemy.getDamage());
            enemy.setHP(enemy.getHP() - player.getDamage());
        }

        int loot = 0;
        if (player.isAlive()) {
            loot = enemy.getGold();
            enemy.loot(player);
        }

        return loot;
    }
```
```java
public class AsciiReader {
    static public List<String> readAscii(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        URL resource = AsciiReader.class.getResource(path);
        assert resource != null;
        BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));

        for(String line; (line = br.readLine()) != null;)
            lines.add(line);

        return lines;
    }
}
```
```java
public void use(Item item) {
        if(!inventory.getItems().contains(item)) {
            return;
        }

        if (item instanceof PotionItem) {
            setHP(hp + item.getValue());
            inventory.removeItem(item);
        }

        if (item instanceof CombatItem) {
            int i;
            for(i = 0; i < inventory.size(); i++) {
                if(inventory.getItem(i).equals(item)) {
                    break;
                }
            }

            if(inventory.getItem(i).isUsed()) {
                inventory.getItem(i).setUsed(false);
                setDamage(damage - item.getValue());
                inventory.refreshOptions();
                return;
            }

            for(int j = 0; j < inventory.size(); j++) {
                if (inventory.getItem(j).isUsed() && inventory.getItem(j) instanceof CombatItem) {
                    inventory.getItem(j).setUsed(false);
                    setDamage(damage - inventory.getItem(j).getValue());
                }
            }
            setDamage(item.getValue() + damage);
            inventory.getItem(i).setUsed(true);
            inventory.refreshOptions();
        }
    }
}
```

### PLANNED FEATURES
- **Raridade items** - Towns com lojas com items que apesar de mais caros são raros e mais fortes
- **Lista Items Default** - List de items default onde os items podem ou não ser alterada consoante o comportamento da Town (mudança de preço e/ou raridade)
- **Dano ao fugir** - Probabilidade de num caso de fuga levar dano
- **Town antes de lutar** - Probabilidade de um town aparecer antes de defrontar todos os inimigos de uma wild
- **Novos locais Town** - Locais onde pode interagir de mais diversas formas como por exemplo melhorar o armamento
- **Sorte na wild** - Items perdidos que podem ser encontrados ao andar pela wild
- **Quests** - Missões que o player poderia cumprir em troca de recompensas


### DESIGN
#### CADA FASE DO JOGO DEVE TER O SEU PRÓRIO ESTADO E COMPORTAMENTO
**Problem in Context**
Tendo em conta a nossa visão do jogo, concordamos que para obter a melhor implementação possível seria para cada fase presente no jogo, esta possuir um comportamento unico, ou seja um _State_. Assim cada fase do jogo teria as suas semelhanças, mas ao mesmo tempo com uma facilidade de lhe ser adicionado as suas próprias características.

**The Pattern**
Utilizamos assim o **State** pattern. Com isto conseguimos que cada fase do jogo possua o seu próprio comportamento e que o jogo apenas tenha de alternar entre estados para se ir desenvolvendo. Sendo apenas necessário uma classe que cuide dos estados implementados, torna-se mais fácil interpretar o código e também a facilidade de criação de novos cenários ou _features_.

**Implementation**

![image](https://user-images.githubusercontent.com/93836408/209411569-8a3b088b-fd53-4059-b979-6866cbf2b6e6.png)

**Consequences**
O uso do Strategy Pattern traz os seguintes benifícios:
- Permite que se criem novos states mais fácilmente
- Torna a transição entre states mais percetível
- Evita a utilização de condições

#### DIFERENTES "TOWNS" TEM LOJAS COM PREÇOS DIFERENTES
**Problem in Context**
Decidimos que, baseado em vários rpgs, nem todas as Towns deveriam apresentar os mesmos valores de preços e raridade. Com isso, ao invés de os alterar diretamente no construtor decidimos implementar o strategy pattern para melhorar a leitura e possiveis adições de outras estratégias. Conseguimos assim tornar o nosso código mais flexível a mudanças de comportamento no que diz respeito as caracteristicas dos items. 

**The Pattern**
Para isto decidimos usar o **Strategy** pattern. Este permite-nos que classes difiram apenas no seu comportamento e que consiga ser alterado em "run time". Neste design pattern conseguimos alterar facilmente o comportamento de um objeto consoante a strategy. Este pattern facilitou-nos a resolução do nosso problema, pois conseguimos alterar o comportamento de uma Town, tornando mais interativa as lojas ao apresentarem diferentes preços ao player

**Implementation**

![image](https://user-images.githubusercontent.com/93836408/204079847-0df94821-a0a9-4f4b-993a-562486438b83.png)

**Consequences**
O uso do Strategy Pattern traz os seguintes benifícios:
- Permite que as Towns se diferenciem em termos de custo e raridade;
- Fornece diferentes implementações e facilita novas que sejam desejadas no futuro;
- Reduz o número de condições que teriam de ser utilizadas através do encapsulamento dos comportamentos em cada classe e do uso de polimorfismo;


#### EXISTEM DIFERENTES CATEGORIAS DE ITEMS
**Problem in Context**
Consideramos que os items deveriam ser rotulados consoante o seu tipo, pois estes podiam ter diferentes responsabilidades e atributos. Optamos assim por utilizar o factory method pattern nos items que melhora a leitura e a manutenção de código como também facilita a adição ou remoção de algum tipo num futuro ou noutra fase do jogo.

**The Pattern**
Como referido em cima utilizamos o **Factory Method** patern. Com este design pattern damos liberdade à subclasse de decidir qual quer instanciar através de uma interface ou de uma classe abstrata. Conseguimos com isto, também, esconder a lógica, da criação dos objetos, do cliente.

**Implementation**

![image](https://user-images.githubusercontent.com/93836408/204081445-4f4a9e5e-ef5d-4a82-a377-ab9f3e656b69.png)

**Consequences**
- Facilidade na reutilização de código
- Promove o Loose Coupling
- Aumenta o numero de classes utilizadas


### KNOWN CODE SMELLS AND REFACTORING SUGGESTIONS
Consideramos que na classe shop podemos encontrar um code smell na característica de _bloater_, pois como na definição deste, possuímos uma classe extensa e complexa que torna difícil o trabalho sobre esta, mais prórpiamente pelo facto de encontrarmos uma classe extensa e com demasiados parâmetros nos seus metodos. No entanto, devido à utilização da estrutura do MVC, não consideramos possivel um refactor possível para resolver este code smell.

### TESTING
![image](https://user-images.githubusercontent.com/93836408/204087956-ef296a17-c8eb-4d71-a0dd-321a06c26d4b.png)





### SELF-EVALUATION
João Coutinho 33%
Joaquim Cunha 33%
Miguel Garrido 33%



