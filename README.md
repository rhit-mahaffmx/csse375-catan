# S1G4 Catan BVA Analysis
## Game Initialization
### Select Number of Players
The following edge cases must be considered when choosing player count
    
    * -1 players are chosen
    * 0 players are chosen
    * 1 player is chosen
    * 2 players are chosen
    * 4 players ar chosen
    * 5 players are chosen
### Determine Turn Order
The following edge cases must be considered when determining turn order

    * Two or more players roll the same highest number
    * Turn order follows counterclockwise
## Gameplay
### Turns
The following edge cases must be considered on a player’s turn
https://github.com/rhit-csse376/S1G4-Catan/pull/20

    * The player rolls before ending their turn
    * The player ends turn without rolling (illegal)
### A Player Rolls Dice
The following edge cases must be considered when rolling a die https://github.com/rhit-csse376/S1G4-Catan/pull/21

    * A player rolls a 1
    * A player rolls a 6
    * A player rolls a 0 (illegal)
    * A player rolls a 7 (illegal)

2 dice https://github.com/rhit-csse376/S1G4-Catan/pull/21

    * The player rolls a 1 (illegal)
    * The player rolls a 2 (lowest legal)
    * The player rolls an 12 (highest legal)
    * The player rolls a 13 (illegal)
### Resource Production
The following edge cases must be considered when a value is rolled
Number of buildings BVA https://github.com/rhit-csse376/S1G4-Catan/pull/22

    * The player has 0 settlements bordering a same-valued tile (lowest legal)
    * Ensure the player does not pick up cards
    * The player has 5 settlements and 1 city bordering a same-valued tile (highest number of buildings)
    * Ensure the player picks up 7 cards corresponding to same-valued resource
Amount payout BVA (NOTE: lowest legal covered by zero settlements case above) https://github.com/rhit-csse376/S1G4-Catan/pull/22

    * The player has 4 cities and 1 settlement bordering a same-valued tile (highest card gain)
    * Ensure the player picks up 9 cards corresponding to same-valued resource
### A Player Wins the Game
The following edge case must be considered on a player’s turn

    * The player has 10 victory points (win)
    * The player has 9 victory points (nothing happens)
    * The player has 11 victory points (win)
### Add victory Point
The following edge cases must be considered when adding a victory point for a player [0, 12) https://github.com/rhit-csse376/S1G4-Catan/pull/28

    * The player has 0 victory points
    * The player has 11 victory points (max score found here: scoring - What is the highest achievable score in Catan - Board & Card Games Stack Exchange)
    * The player has 12 victory points (13 would be an illegal state)
### Deduct 2 Victory Points (longest road, largest army)
The following edge cases must be considered when removing victory points (0, 12] https://github.com/rhit-csse376/S1G4-Catan/pull/42

    * The player has 0 victory points
    * The player has 1 victory point
    * The player has 12 victory points
### Place Settlement
The following edge cases must be considered when a player tries to place a settlement 
https://github.com/rhit-csse376/S1G4-Catan/pull/15 , https://github.com/rhit-csse376/S1G4-Catan/pull/17

    * The player has 0 brick, and at least 1 of each lumber, wool, and grain (illegal)
    * The player has 0 lumber, and at least 1 of each brick, wool, and grain (illegal)
    * The player has 0 wool, and at least 1 of each brick, lumber, and grain (illegal)
    * The player has 0 grain, and at least 1 of each brick, lumber, and wool (illegal)
    * The player has at least one of each brick, lumber, wool, and grain and:
    * The player places it on a path (illegal)
        * The player places it on an intersection where they do not have a bordering road (illegal)
        * The player places it on an intersection where they have a bordering road and at least one of the 3 adjacent intersections is non-empty (illegal)
        * The player places it on an intersection where they have a bordering road and all 3 adjacent intersections are empty (legal)
### Upgrade Settlement to City
The following edge cases must be considered when a player tries to upgrade a city to a settlement https://github.com/rhit-csse376/S1G4-Catan/pull/24

    * The player does has less than 3 ore and less than 2 grain (illegal)
    * The player has less than 3 ore and at least 2 grain (illegal)
    * The player has at least 3 ore and less than 2 grain (illegal)
    * The player has at least 3 ore and at least 2 grain (illegal)
### Place Road
The following edge cases must be considered when a player tries to build a road https://github.com/rhit-csse376/S1G4-Catan/pull/17

    * The player has 0 lumber, and at least 1 brick (illegal)
    * The player has 0 brick, and at least 1 lumber (illegal)
    * The player has at least one brick and lumber and:
        * The player places the road on an unconnected (no connecting road or settlement) tile length (illegal)
        * The player places the road on an existing road (illegal)
### Purchase Development Card
The following edge cases must be considered when a player tries to buy a development card
https://github.com/rhit-csse376/S1G4-Catan/pull/19

    * The deck supply is empty
    * The player has 0 ore, and at least one of each wool and grain (illegal)
    * The player has 0 wool, and at least one of each ore and grain (illegal)
    * The player has 0 grain, and at least one of each ore and wool (illegal)
    * The player has at least one of each ore, wool, and grain (illegal)
### Use Road Building Card
The following edge cases must be considered for when a player uses a road building card
https://github.com/rhit-csse376/S1G4-Catan/pull/33

    * The player has 1 valid road path
    * The player has 0 valid road paths (illegal)
    * The player has 2 valid road paths
### Use Year of Plenty Card
The following edge cases must be considered for when a player uses a year of plenty card
https://github.com/rhit-csse376/S1G4-Catan/pull/33

    * The player picks two of the same type of card
    * The player picks one type of different cards
### Use Monopoly Card
The following edge cases must be considered for when a player uses a monopoly card
https://github.com/rhit-csse376/S1G4-Catan/pull/35

    * No other player has that type of resource
    * At least one player has that type of resource
### Use Knight Card
The following edge cases must be considered for when a player uses a knight card
https://github.com/rhit-csse376/S1G4-Catan/pull/42

    * There is no settlement adjacent to the robber’s new hex
    * The same hex is chosen that the robber already resides on (illegal)

### A 7 is rolled (robber)
The following edge cases must be considered for players losing resource cards when a player rolls a 7
https://github.com/rhit-csse376/S1G4-Catan/pull/31

    * No players have more than 7 resource cards
    * At least 1 player has more than 7 resource cards

The following edge cases must be considered for a player moving a robber

    * The player moves the robber to the desert
    * The player moves the robber to a terrain hex with no adjacent settlements (no steal)
    * The player moves the robber to a terrain hex with exactly 1 adjacent settlement (steal, no need to choose the player)
    * The player moves the robber to a terrain hex with at least one adjacent settlement from at least 2 separate players (steal, player gets to choose who to steal from)

### A Player Discards Cards
The following edge cases must be considered for the hand size of the discarding player
https://github.com/rhit-csse376/S1G4-Catan/pull/29

    * The player has 7 cards
    * The player has 8 cards
    * The player has 6 cards
The following edge cases must be considered for players choosing cards to discard

    * The player chooses the correct number of cards
    * The player chooses one cards than needed (illegal)
    * The player chooses one less card than needed (illegal)

### A Player Engages in Maritime Trade
The following edge cases must be considered for a player engaging in maritime trade
https://github.com/rhit-csse376/S1G4-Catan/pull/43

    * The player does not have a settlement on a harbor, but trades using harbor rate
    * The player has settlements on >1 harbor
    * The player trades the correct amount of an incorrect resource (illegal)
    * The player trades the insufficient amount of a correct resource (illegal)
    * The player trades an indivisible amount of a resource (i.e. 7 lumber in a 2:1 trade)

### A Player engages in Domestic Trade
The following edge cases must be considered for a player engaging in domestic trade
https://github.com/rhit-csse376/S1G4-Catan/pull/25

    * The player does not have any resources (illegal)
    * No other players accept a trade
    * A different player counter offers
    * The player denys the counter offer

### A player draws a victory card
The following edge cases must be considered when a player draws a victory card
https://github.com/rhit-csse376/S1G4-Catan/pull/33

    * The player is at 2 victory points (game does not end)
    * The player is a 9 victory points (game ends)

### A Player has Longest Road
The following edge cases must be considered when a player has longest road
https://github.com/rhit-csse376/S1G4-Catan/pull/33

    * The player has a continuous path of length 4 (nothing occurs)
    * The player has a continuous path of length 5 and the longest road (smallest value)
    * Ensure the player gains 2 victory points
    * The player has a continuous path of length 56 and the longest road (max value)
    * Ensure the player gains 2 victory points
    * Another player builds a continuous path with a longer length (lose longest road)
    * Ensure the player loses 2 victory points

### A Player has Largest Army
The following edge cases must be considered when a player gains longest army
https://github.com/rhit-csse376/S1G4-Catan/pull/42

    * The player has 2 knight cards (nothing occurs)
    * The player has 3 knight cards and has highest amount played (smallest value)
    * Ensure the player gains 2 victory points
    * The player has 14 knight cards and has highest amount played (highest value)
    * Ensure the player gains 2 victory points
    * Another player plays more >=3 knight cards and has more than you
    * Ensure the player loses 2 victory points
