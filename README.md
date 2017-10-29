# Plexus
### By Santiago Benoit
Plexus is a simple networking strategy game for 2-4 players. It can be played in singleplayer mode against AI players or in multiplayer mode.

## How to Play
Plexus is played on a randomly generated network of interconnected nodes. The objective of the game is to acquire the greatest number of connections between nodes as possible before the game ends. The game ends when the next player can no longer make any moves (even if other players still can).

At the start of the game, each player chooses their starting position, starting from the first player. Starting positions cannot be directly connected to each other. After all players have chosen their starting positions, the game proceeds in reverse turn order, starting from the last player. This is done to balance the advantage between choosing the starting position first and going first.

On your turn, you may claim an unclaimed node that is directly connected to one of your owned nodes. Each connection between your owned nodes will gain you one point. When the game ends, if there is no tie for first place, whoever has the most points wins!

## Game Setup
- Singleplayer: All players other than player 1 will be controlled by AI.
- Multiplayer: Humans only.
- Players: Total number of players that will participate in the game. (1 - 4)
- Nodes: Number of nodes to spawn on the map. (Minimum 5)
- Density: A higher density will spawn nodes closer together and with more connections. (0.1 - 1.0)

## Settings
- Enable Audio: Toggle sound on and off.
- Fullscreen Mode: Toggle between fullscreen and windowed mode.
- Viewport Width/Height: Width/height of the game window. (100 - screen width/height)
- Zoom Exponent: A higher value will allow for a closer zoom. (0.5 - 2.0)

![Plexus Screenshot](/images/plexus_screenshot.png?raw=true)
