# AGENTS.md

This file defines the working rules for AI coding agents contributing to the InOutManager project.

The project is developed through small GitHub Issues and Pull Requests, with architecture, workflow, roadmap, and completion rules managed in `docs/project-management/`, and AI working protocol managed in `docs/ai/`.


This repository currently uses a documentation-driven agent workflow.  
Pull request templates and GitHub Actions CI may be added later, but agents must not assume that automated PR or CI checks are already configured.

> Do not edit code before producing an implementation plan and receiving explicit user approval.

---

## 1. Core principle: staged work with human approval gates

AI agents do NOT implement an Issue end-to-end in a single step.

All work follows the staged protocol defined in `docs/ai/issue-workflow.md`:

```text
Stage 1: Analysis        → output analysis, no code changes
Stage 2: Plan            → output plan using docs/ai/plan-template.md, no code changes
         [HUMAN APPROVAL GATE]
Stage 3: Implementation  → small units, following the approved plan only
Stage 4: Verification    → output report using docs/ai/verification-report-template.md
```

Hard rules:

- Do not modify any code before the human approves the Stage 2 plan.
- Do not exceed the approved plan during Stage 3. If new work is discovered, stop and report it.
- Do not report completion without a Stage 4 verification report.
- If the human request skips stages, ask which stage to start from instead of assuming.
- AI agents must treat the current GitHub Issue as a contract.
- The agent must implement only the current Issue scope.
- If a change is useful but not required by the Issue, do not implement it. Write it as a follow-up recommendation instead.

## 2. Required documents

Before starting work on any Issue, read the target GitHub Issue and the following project documents.

Required entry document:

- `docs/ai/issue-workflow.md`
- `docs/ai/issue-work-rules.md`
- `docs/project-management/issue-workflow.md`

Supporting documents:

- `docs/project-management/project-roadmap.md`
- `docs/project-management/architecture-rules.md`
- `docs/project-management/definition-of-done.md`

Do not copy these documents into the Issue, completion report, or PR body. Use them as working references.

---

## 3. Required approval-gated workflow

Every Issue must follow this order.

### Step 1. Analysis only

Before proposing code changes, analyze only.

In Stage 1, the agent reads and reports. No code changes.

The analysis must include:

- Read `docs/ai/issue-workflow.md'
- Target Issue summary
- Current branch state compared with main
- Relevant documents checked
- In-scope changes
- Out-of-scope changes
- Risk and ambiguity
- Whether the Issue is ready for implementation
- Conflicts or ambiguity between the Issue and `docs/project-management/architecture-rules.md`
- Questions that need human answers before planning

If the Issue conflicts with architecture rules, stop and ask. Do not resolve the conflict by choosing a direction unilaterally.

### Step 2. Implementation plan only

Before editing files, produce an implementation plan.

In Stage 2, the agent produces a plan using `docs/ai/implementation-plan-template.md`. No code changes.

The plan must include:

- Modified files list
- Files that must not be modified
- Layer impact
- Acceptance criteria mapping
- Commit plan
- Validation commands
- Manual behavior checks
- Follow-up candidates that will not be implemented in this Issue

No code edits are allowed in this step.
The agent must not proceed to Stage 3 until the human replies with explicit approval.

### Step 3. User approval

- Implement only what the approved plan describes.
- Work in small units matching the commit plan in the Issue.
- If implementation reveals that the plan was wrong or incomplete, STOP. Report the discrepancy and wait for instructions. Do not improvise.
- Discovered additional work goes to a follow-up Issue note, never into the current changes.

Stop conditions (stop and ask before editing):

- the Issue conflicts with existing architecture rules,
- the task requires Room schema changes not mentioned in the Issue,
- the task requires build configuration or dependency changes not mentioned in the approved plan,
- the implementation would affect more than one Milestone scope,
- the expected file changes are larger than the approved plan.

Note: adding a dependency (e.g. a new artifact in `libs.versions.toml` or `build.gradle.kts`) is a build configuration change. It must appear in the approved plan. If it was missed, stop and report instead of silently adding it.

### Step 4. Verification report

After implementation, write a verification report using:

- `docs/ai/verification-report-template.md`

The report must include:

- Modified files
- Acceptance criteria Pass/Fail
- Out-of-scope change check
- Validation command results
- Manual behavior check results
- Remaining risks
- Follow-up recommendations

Do not claim completion without a verification report.
Do not hide incomplete or uncertain work. If something could not be verified, state it clearly.
Do not claim that CI passed unless a CI system actually ran.

---

## 4. Stop conditions

Stop and ask for clarification before editing when:

- the user has not approved the implementation plan,
- the Issue conflicts with existing architecture rules,
- the task requires Room schema changes that are not mentioned in the Issue,
- the task requires build configuration changes that are not mentioned in the Issue,
- the implementation would affect more than one Milestone scope,
- the task requires combining unrelated Hilt, Navigation, UI, database, or feature work,
- the expected file changes are much larger than the Issue scope,
- an apparently good improvement is outside the Issue scope,
- the agent cannot run or verify a required command,
- a dependency must be added but the Issue did not mention dependency changes,
- the implementation plan becomes inaccurate during development.

---

## 5. Documentation update rules

Update project documents only when the change affects shared rules or future workflow.

Update:

- `docs/project-management/project-roadmap.md` when milestone order, follow-up Issues, or backlog changes.
- `docs/project-management/architecture-rules.md` when architecture boundaries, DI, schema, or layer rules change.
- `docs/project-management/definition-of-done.md` when common completion criteria change.
- `docs/project-management/issue-workflow.md` when Issue writing or working process changes.
- `docs/ai/` when AI workflow, planning, verification, or prompt templates change.
- `AGENTS.md` when AI Agent workflow rules change.

Do not update documentation just to restate implementation details from a single PR.

---

## 6. Future automation notes

The following files may be added later:

```text
.github/pull_request_template.md
.github/workflows/android-ci.yml
scripts/verify_issue_x.sh
```

If these files are added later, update this file and `docs/project-management/definition-of-done.md` so that PR and CI validation rules match the actual repository setup.

---

## 7. Communication rules

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
- undocumented architecture boundary changes,
- claiming completion without verification evidence.

---

## 8. Current project management paths

Project management documents are stored here:

```text
docs/project-management/
```

AI workflow documents are stored here:

```text
docs/ai/
```

Use these files as the source of truth for Issue workflow, roadmap, architecture rules, completion criteria, AI workflow, implementation planning, and verification reporting.
