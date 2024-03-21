# AnchoredDropdownMenu

`AnchoredDropdownMenu` lets you override the vertical anchoring option that
`androidx.compose.material3.DropdownMenu` chooses automatically.

## Motivation

As of `androidx.compose.material3:material3:1.2.1` you have to use the regular `DropdownMenu`
instead of `ExposedDropdownMenu` inside `ExposedDropdownMenuBox`es if you want to
[match the `TextField`'s width](https://issuetracker.google.com/issues/205589613) or
[make it editable](https://stackoverflow.com/q/76039608).

Unfortunately, when you use an editable `TextField` as a search filter you may have many
`DropdownMenuItem`s which leads to the `DropdownMenu` covering the `TextField` to gain more space
and that obscures what the user is typing.

`ExposedDropdownMenu` has been reimplemented
in [`1.3.0-alpha03`](https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-alpha03)
and it fixes all the above issues, so `AnchoredDropdownMenu` will no longer be necessary for the
aforementioned use-case when `1.3.0` becomes stable.

However, it may still be used for other scenarios where manually specifying a vertical anchoring
point is useful.

## Installation

[![](https://jitpack.io/v/denis-ismailaj/AnchoredDropdownMenu.svg)](https://jitpack.io/#denis-ismailaj/AnchoredDropdownMenu)

1. Add the JitPack repository to your project's build or settings file

        dependencyResolutionManagement {
            ...
            repositories {
                ...
                maven("https://jitpack.io")
            }
        }

2. Add the `AnchoredDropdownMenu` dependency to your module's build file

        dependencies {
            ...
            implementation("com.github.denis-ismailaj:AnchoredDropdownMenu:1.0.0")
        }

## Usage

`AnchoredDropdownMenu` has all the same parameters as `DropdownMenu` but with the addition
of `anchor`:

      import androidx.compose.material3.AnchoredDropdownMenu
      import androidx.compose.material3.DropdownMenuAnchor

      AnchoredDropdownMenu(
         ...
         anchor = DropdownMenuAnchor.TopToAnchorBottom,
      ) { 
         ... 
      }

The available `anchor` options are: `Auto`, `TopToAnchorBottom`, `BottomToAnchorTop`, 
`CenterToAnchorTop`, `TopToWindowTop`, `BottomToWindowBottom`, `AutoToWindow`.

When `anchor` is not set, it defaults to `Auto` which retains the original `DropdownMenu` behavior.
