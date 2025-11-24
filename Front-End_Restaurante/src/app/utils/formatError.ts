export function formatError(err: unknown, fallback = 'Ocorreu um erro'): string {
    if (!err) return fallback;
    if (typeof err === 'string') return err;
    if (err instanceof Error) return err.message || fallback;
    const anyErr = err as { error?: unknown; message?: string };
    if (anyErr.error)
        return typeof anyErr.error === 'string'
        ? anyErr.error
        : JSON.stringify(anyErr.error);
    if (anyErr.message) return anyErr.message;
    try {
        return JSON.stringify(err);
    } catch {
        return fallback;
    }
}