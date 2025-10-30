# MinerMC Global Boost

A Spigot plugin that provides global mining boost multipliers with timer-based activation and personal boost inventory system.

## Features

- **Global Mining Boosts**: 2x, 3x, or 4x multipliers for all minable blocks
- **Timer-Based**: Boosts run for a set duration with countdown
- **Boss Bar Display**: Shows active boost and remaining time
- **Boost Inventory**: Give players boosts they can activate later
- **Compatible with DeluxeMines**: Works with mine reset plugins
- **Persistent**: Boosts survive server restarts

## Commands

### For OPs:

| Command | Description | Example |
|---------|-------------|---------|
| `/boost <2x\|3x\|4x> <minutes>` | Activate a global boost | `/boost 2x 30` |
| `/boost off` | Stop the current boost | `/boost off` |
| `/giveboost <player> <2x\|3x\|4x> <30m\|1h>` | Give a boost to a player | `/giveboost Steve 3x 1h` |

### For Players:

| Command | Description | Example |
|---------|-------------|---------|
| `/useboost <2x\|3x\|4x> <30m\|1h>` | Use a boost from inventory | `/useboost 2x 30m` |
| `/useboost` | View your boost inventory | `/useboost` |

## How It Works

1. **OPs activate boosts** directly with `/boost` or give them to players with `/giveboost`
2. **Players use boosts** from their inventory with `/useboost`
3. **Boosts are global** - everyone on the server benefits
4. **Timers stack** - using the same boost type adds time
5. **Different boosts don't mix** - must turn off current boost first

## Permissions

- `minermc.boost.use` - Use `/boost` command (default: OP)
- `minermc.giveboost.use` - Use `/giveboost` command (default: OP)
- `minermc.useboost.use` - Use `/useboost` command (default: all players)

## Installation

1. Download the plugin JAR from releases
2. Place in your server's `plugins/` folder
3. Restart the server
4. Configure permissions if needed

## Configuration

- `config.yml` - Stores active boost state
- `boosts.yml` - Stores player boost inventories

## Requirements

- Spigot/Paper 1.21+
- Java 21+

## Building

```bash
./gradlew build
```

JAR output: `build/libs/MinerMCGlobalBoost-1.0-SNAPSHOT.jar`
