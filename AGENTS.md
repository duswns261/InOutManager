# AGENTS.md

This file defines the working rules for AI coding agents contributing to the InOutManager project.

InOutManager is an Android inventory management app project.  
The project is developed through small GitHub Issues and Pull Requests, with architecture, workflow, roadmap, and completion rules managed in `docs/project-management/`.

This repository currently uses a documentation-driven agent workflow.  
Pull request templates and GitHub Actions CI may be added later, but agents must not assume that automated PR or CI checks are already configured.

---

## 1. Required documents

Before starting work on any Issue, read the target GitHub Issue and the following project documents.

Required entry document:

- `docs/project-management/issue-workflow.md`

Supporting documents:

- `docs/project-management/project-roadmap.md`
- `docs/project-management/architecture-rules.md`
- `docs/project-management/definition-of-done.md`

Do not copy these documents into the Issue, completion report, or PR body. Use them as working references.

---

## 2. Pre-work checklist

Before editing files, summarize the following.

- Target Issue
- Expected files to change
- Files, layers, or features that must not change
- Relevant project-management documents
- Required local validation commands
- Possible risks or ambiguity

If the Issue conflicts with `docs/project-management/architecture-rules.md`, stop and clarify before editing.

---

## 3. Required workflow

When working on an Issue, follow this order.

1. Read the target GitHub Issue.
2. Read `docs/project-management/issue-workflow.md`.
3. Check `docs/project-management/project-roadmap.md` if the task affects milestone order, excluded future work, or backlog.
4. Check `docs/project-management/architecture-rules.md` before changing package structure, ViewModel, UseCase, Repository, DataSource, Room, DI, Navigation, or Compose UI state.
5. Check `docs/project-management/definition-of-done.md` before finalizing the task.
6. Keep the change scope limited to the target Issue.
7. Do not combine unrelated future Issues into the current task.
8. Run the validation commands listed in the Issue before reporting completion or opening a PR.

---

## 4. Stop conditions

Stop and ask for clarification before editing when:

- the Issue conflicts with existing architecture rules,
- the task requires Room schema changes that are not mentioned in the Issue,
- the task requires build configuration changes that are not mentioned in the Issue,
- the implementation would affect more than one Milestone scope,
- the task requires combining unrelated Hilt, Navigation, UI, database, or feature work,
- the expected file changes are much larger than the Issue scope.

---

## 5. Issue scope rules

Each Issue should remain small and reviewable.

Do not include unrelated work such as:

- applying Hilt while working on StateFlow,
- applying Navigation while working on DI,
- changing Room schema during a presentation-layer refactor,
- adding new features during architecture cleanup,
- changing UI design during dependency or state management work.

If additional work is discovered, document it as a follow-up Issue instead of expanding the current Issue.

---

## 6. Architecture rules

Follow `docs/project-management/architecture-rules.md`.

Core rules:

- Presentation should not depend directly on data implementation classes.
- ViewModel should depend on UseCases or state holders, not Repository implementations.
- Domain should not depend on presentation or data implementation details.
- Data layer should contain Room, DataSource, Repository implementation, and mapper details.
- UI state models such as `InventoryUiState` belong to the presentation layer.
- Room schema changes must be intentional and verified.

---

## 7. Dependency injection rules

Follow the current milestone strategy.

- Before Hilt migration, keep the existing manual DI structure unless the Issue explicitly says otherwise.
- During Hilt migration, do not mix Navigation, UI redesign, or unrelated state management changes.
- After Hilt migration, use the project’s Hilt modules and injection rules consistently.
- Keep Preview and fake dependency creation paths working when possible.

---

## 8. Navigation rules

Navigation changes should be handled only in Navigation-related Issues.

- Do not introduce Navigation Compose in non-navigation Issues.
- Do not redesign screen flow unless the Issue explicitly includes it.
- Keep ViewModel state ownership and screen state collection consistent with the architecture rules.

---

## 9. Room and schema rules

Room schema changes must not happen accidentally.

Before finalizing work, check whether any of the following changed:

- Room Entity
- DAO
- AppDatabase
- Database version
- `app/schemas`

If the current Issue is not intended to change the database schema, there should be no schema diff.

---

## 10. Local validation rules

Before reporting completion or opening a PR, confirm the following.

- The app builds successfully.
- The Issue completion conditions are satisfied.
- The change scope matches the Issue.
- Existing core behavior still works.
- No unintended Room schema change occurred.
- Required project documents were checked.
- The completion report or PR body can clearly explain what changed and how it was verified.

At minimum, run locally:

```bash
./gradlew :app:build
```

When available and relevant, also run locally:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:lintDebug
git diff -- app/schemas
```

Also run any grep or diff commands listed in the target Issue.

Do not claim that CI passed unless a GitHub Actions workflow or another CI system actually ran successfully.

---

## 11. Completion report and PR requirements

A completion report or PR should explain the result of the work, not repeat the full Issue body.

Include:

- Summary of changes
- Review points
- Validation results
- Screenshots if UI changed
- Follow-up notes if needed

Do not hide incomplete or uncertain work. If something could not be verified, state it clearly.

---

## 12. Documentation update rules

Update project documents only when the change affects shared rules or future workflow.

Update:

- `docs/project-management/project-roadmap.md` when milestone order, follow-up Issues, or backlog changes.
- `docs/project-management/architecture-rules.md` when architecture boundaries, DI, schema, or layer rules change.
- `docs/project-management/definition-of-done.md` when common completion criteria change.
- `docs/project-management/issue-workflow.md` when Issue writing or working process changes.
- `AGENTS.md` when AI Agent workflow rules change.

Do not update documentation just to restate implementation details from a single PR.

---

## 13. Future automation notes

The following files may be added later, but they are not required for the current documentation-driven workflow.

```text
.github/pull_request_template.md
.github/workflows/android-ci.yml
```

If these files are added later, update this file and `docs/project-management/definition-of-done.md` so that PR and CI validation rules match the actual repository setup.

---

## 14. Communication rules

When reporting work, be concise and specific.

Prefer:

- what changed,
- why it changed,
- what was verified,
- what remains.

Avoid:

- broad refactoring without Issue approval,
- large unrelated changes,
- speculative future work inside the current PR,
- undocumented architecture boundary changes.

---

## 15. Current project management path

Project management documents are stored here:

```text
docs/project-management/
```

This folder contains:

```text
issue-workflow.md
project-roadmap.md
architecture-rules.md
definition-of-done.md
```

Use these files as the source of truth for Issue workflow, roadmap, architecture rules, and completion criteria.
