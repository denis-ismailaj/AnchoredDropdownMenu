# AnchoredDropdownMenu

`AnchoredDropdownMenu` lets you override the vertical anchoring option that
`androidx.compose.material3.DropdownMenu` chooses automatically.

## Installation

[![](https://jitpack.io/v/denis-ismailaj/AnchoredDropdownMenu.svg)](https://jitpack.io/#denis-ismailaj/AnchoredDropdownMenu)

1. Add the JitPack repository to your project build or settings file

        dependencyResolutionManagement {
            ...
            repositories {
                ...
                maven("https://jitpack.io")
            }
        }

2. Add the `AnchoredDropdownMenu` dependency to your module build file

        dependencies {
            ...
            implementation("com.github.denis-ismailaj:AnchoredDropdownMenu:1.0.0")
        }

## Usage

`AnchoredDropdownMenu` uses all the same options as `DropdownMenu` but with the addition of the 
`anchor` parameter.

      import androidx.compose.material3.AnchoredDropdownMenu
      import androidx.compose.material3.DropdownMenuAnchor

      AnchoredDropdownMenu(
         ...
         anchor = DropdownMenuAnchor.TopToAnchorBottom,
      ) { 
         ... 
      }

The available options are: `Auto`, `TopToAnchorBottom`, `BottomToAnchorTop`, `CenterToAnchorTop`,
`TopToWindowTop`, `BottomToWindowBottom`, `AutoToWindow`.

When `anchor` is not set, it defaults to `Auto` which retains the original `DropdownMenu` behavior.
