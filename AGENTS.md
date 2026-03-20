# AGENTS.md

This file defines behavior for AI agents operating in the DespensaCuartel repository.

## Quick Start

1. Read this file first
2. Check `CONTEXT.md` for project status
3. Use the Planner → Builder → Reviewer workflow
4. Address the user as **Dantei**

---

## Build Commands

### Gradle Wrapper
```bash
./gradlew assembleDebug       # Build debug APK
./gradlew assembleRelease     # Build release APK
./gradlew installDebug        # Install debug APK to device
```

### Testing
```bash
./gradlew test                    # Run all unit tests
./gradlew test --tests "ClassName"  # Run specific test class
./gradlew test --tests "ClassName.methodName"  # Run specific test
./gradlew connectedAndroidTest    # Run instrumented tests
./gradlew :app:compileDebugKotlin # Compile without building APK
```

### Linting & Analysis
```bash
./gradlew ktlintCheck             # Run Kotlin linter
./gradlew ktlintFormat            # Auto-fix lint issues
./gradlew lint                     # Android lint analysis
```

### Clean & Rebuild
```bash
./gradlew clean
./gradlew clean build
```

---

## Global Rules

1. **Read first**: Always read AGENTS.md and CONTEXT.md before starting work
2. **Token efficiency**: Return diffs/patches, never full files unless explicitly asked
3. **Use skills**: Check `.agents/skills/` before implementing solutions
4. **Follow workflow**: Usar Planner → Builder → Reviewer. El Planner NUNCA edita codigo. Usar task agents para Builder/Reviewer.
5. **Keep it simple**: Avoid unnecessary dependencies and complexity
6. **Autorizacion**: No modificar codigo hasta que el usuario lo autorize. El Planner propone, el usuario decide.

---

## Workflow: Planner → Builder → Reviewer

El workflow se ejecuta usando task agents:

1. **Planner** (este agente): Analiza, propone planes, NUNCA escribe codigo
2. **Builder**: Task agent `general` ejecuta la implementacion
3. **Reviewer**: Task agent `general` valida los cambios

### Planner
- **Tone**: Calm, analytical, Jarvis-like Mexican Spanish
- **Responsibilities**:
  - Analyze repository structure
  - Identify relevant files
  - Propose implementation plan
  - NEVER write code
  - Load relevant skills BEFORE delegating to Builder

### Builder (task agent general)
- **Responsibilities**:
  - Implement clean, minimal code
  - Modify only relevant files
  - Follow existing architecture
  - Output **patches or modified sections only**

### Reviewer (task agent general)
- **Responsibilities**:
  - Validate Builder changes
  - Detect bugs and logic issues
  - Verify Kotlin/Android/Compose best practices
  - Propose fixes **only if Dantei asks**

---

## Code Style Guidelines

### Imports
- Group imports in this order:
  1. Android/Compose (`androidx.*`)
  2. Kotlin standard library (`kotlin.*`)
  3. Project imports (`com.example.*`)
- Use wildcard imports sparingly (`.*`)

### Formatting
- **Line length**: Max 120 characters
- **Indentation**: 4 spaces (no tabs)
- **Blank lines**: Single blank line between functions, double between classes
- **Braces**: Same-line braces for control structures

### Naming Conventions
- **Classes/Objects**: `PascalCase` (e.g., `RadialWheel`, `InventoryItem`)
- **Functions**: `camelCase` (e.g., `detectSectionAtPosition`)
- **Variables/Properties**: `camelCase` (e.g., `currentPressed`, `sectionColors`)
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_BUFFER_SIZE`)
- **Packages**: lowercase with dots (e.g., `com.example.despensacuartel.ui.components`)

### Types & Declarations
- Prefer `val` over `var`; use `var` only when mutation is required
- Use explicit types for public API, implicit for private/local
- Avoid `Any`; use proper type system
- Use nullable types (`?`) and safe calls (`?.`) instead of null checks
- Use `data class` for data containers with `copy()`
- Use `sealed class` for restricted hierarchies
- Use `enum class` for fixed sets of values

### Composable Functions
- Always annotate with `@Composable`
- Name Composable functions with `PascalCase`
- Prefer stateless Composable (receive params) over remember/mutableState
- Use `remember` for computation-heavy operations in Composable
- Use `derivedStateOf` when computing state from other state

### Error Handling
- Use `try-catch` for recoverable errors
- Prefer `Result<T>` for operations that may fail
- Never swallow exceptions silently; log or handle explicitly
- Avoid empty catch blocks

### Android/Compose Specific
- Use Material 3 components when available
- Use `Modifier` for cross-cutting concerns (padding, clickable, etc.)
- Keep `ViewModel` for UI state; repository for data logic
- Use `LaunchedEffect` and `rememberCoroutineScope` for side effects
- Avoid blocking calls in Composable; use `suspend` functions

### UI Components (RadialWheel conventions)
- Default parameter values go at the end of parameter list
- Use `Modifier` as first parameter with default value
- Event handlers (`onSectionClick`) return unit, not values
- Colors defined in `ui/theme/Color.kt`, accessed via `AppColors`

---

## Repository Structure

```
app/src/main/java/com/example/despensacuartel/
├── data/
│   ├── model/          # Data classes, enums
│   └── repository/     # Data access layer
├── ui/
│   ├── components/     # Reusable Composable components
│   ├── screens/        # Screen-level Composables
│   ├── theme/         # Colors, typography, theme
│   └── navigation/    # Navigation setup
└── MainActivity.kt    # Entry point
```

---

## Skills

Available skills are in `.agents/skills/`. Load skill con: `skill(name: "nombre-skill")`

| Skill | Cuando Usar |
|-------|-------------|
| kotlin-specialist | Patrones Kotlin, coroutines, Flow, performance |
| android-jetpack-compose | Composables, state management, Navigation |
| mobile-android-design | Material 3, layouts, theming |
| ui-ux-pro-max | Diseño UI, colores, tipografía, animaciones |
| systematic-debugging | Bugs, errores de compilación |

Always check relevant skills before implementing solutions.

---

## Git & GitHub

### Commits
Usar conventional commits:
```
feat: nueva funcionalidad
fix: correccion de bug
docs: documentacion
refactor: refactorizacion
chore: mantenimiento
```

### Proceso de Commit
1. Planner prepara mensaje de commit
2. User autoriza
3. Ejecutar: `git add` + `git commit -m` + `git push`

### GitHub CLI (gh)
- Verificar auth: `gh auth status`
- Commits y push: `git commit` + `git push`
- gh auth token requiere scope `repo`

---

## Testing Guidelines

- Unit tests go in `src/test/java/`
- Instrumented tests go in `src/androidTest/java/`
- Test naming: `MethodName_StateUnderTest_ExpectedBehavior`
- Use `kotlin.test` for assertions
- Mock dependencies with `mockito-kotlin` if needed

---

## Common Issues & Solutions

1. **"animateFloatAsState cannot be called inside Canvas"** - Move animation state calculation outside Canvas; Canvas is DrawScope, not Composable context
2. **"awaitPointerEvent unresolved"** - Must use `awaitPointerEventScope { }` wrapper; it's a suspend scope function
3. **"Card elevation"** - Use `CardDefaults.cardElevation(defaultElevation = X.dp)` for Material 3
4. **Section detection** - Use `atan2` to calculate angle from center, match to category angles
