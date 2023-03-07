# NotEnoughDynamicLights

> Note: The feature of this mod already exists in [NotEnoughUpdates](https://github.com/NotEnoughUpdates/NotEnoughUpdates)
> there is no need to install this mod if you already have NEU installed. The only exception to this, is if this feature
> has not yet been put into a public release of NEU. Obviously if you don't have NEU installed but want this feature then
> by all means you can install this mod.

**Usage:** /nedl

## What is this?
This mod takes advantage of an OptiFine feature, Dynamic Light Items, which enables certain items to emit a light level 
around the player when holding them. This mod specifically adds a GUI to add specific Skyblock items and normal Minecraft
items to a list which will also emit light whilst being held.

> Since OptiFine also checks the helmet slot for light emitting items, adding an item that can be equipped in the helmet
> slot will also emit light without having to hold a specific or any item at all.

There are some limitations to this mod in that some items cannot be affected by this, specifically items already implemented
with this feature by OptiFine. Which may sound fine since they are already implemented however, the items implemented by OptiFine
have varying (and lower) light levels that they emit, and since this mod sets all items added by it to light level 15, there could be the
case that someone wants an item, already implemented by OptiFine, to emit light of level 15, but since the current implementation
doesn't allow this, the following items cannot be affected by this mod:
* Lava Bucket
* Blaze Rod
* Blaze Powder
* Glowstone Dust
* Prismarine Crystals
* Magma Cream
* Nether Star
* Beacon (I think)

## Using NEU over NEDL

I highly recommend using NEU's implementation of this feature (very similar) since it is more robust than the implementation
in this mod, due to the underlying code that NEU has with resolving ItemStack's, NBT data from its repo, and resolving
Skyblock items internal names which allows for far better config saving than this mods config saving.

## NEU Code

This mod does include code from NEU that was not written by me and is cited in every instance where that code is found,
and is also covered under the GPL-3.0 license in which this mod is also licensed under.