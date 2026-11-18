# SPEC-Driven Development 约束文档
- 参考 Notion Page ID: 37d0ec7a4b9280bd8c84c922f57f6d3e

# 全局研发环境与行为准则
- 参考 Notion Page ID: 37d0ec7a4b928005aee8d863af5b2518

# 开源车联网研发与行为准则
- 参考 Notion Page ID: 37d0ec7a4b92802d8915c7c310e24486

# 当前项目需求文档
- 参考 Notion Page ID: 41c0ec7a4b9280499b15fca3d9828a2c
- 变更记录 Notion Page ID: 41c0ec7a4b92802a915be52bd574554d

# 当前项目设计文档
- 参考 Notion Page ID: 41c0ec7a4b92800ca811d203c03826aa
- 变更记录 Notion Page ID: 41c0ec7a4b928004b782f2731d3829a0

# 当前项目系统架构文档
- 参考 Notion Page ID: 41c0ec7a4b92809da870ce809df8d8e9

# 当前项目功能架构文档
- 参考 Notion Page ID: 41c0ec7a4b9280009a16f9ef0987cea7

# 当前项目数据库文档
- 参考 Notion Page ID: 41c0ec7a4b928077b5ceff19b5ab591f

# 当前项目错误码文档
- 参考 Notion Page ID: 41c0ec7a4b9280558d63cda9ea15c6db

# graphify

This project has a graphify knowledge graph at graphify-out/.

Rules:
- Before answering architecture or codebase questions, read graphify-out/GRAPH_REPORT.md for god nodes and community structure
- If graphify-out/wiki/index.md exists, navigate it instead of reading raw files
- For cross-module "how does X relate to Y" questions, prefer `graphify query "<question>"`, `graphify path "<A>" "<B>"`, or `graphify explain "<concept>"` over grep — these traverse the graph's EXTRACTED + INFERRED edges instead of scanning files
- After modifying code files in this session, run `graphify update .` to keep the graph current (AST-only, no API cost)
