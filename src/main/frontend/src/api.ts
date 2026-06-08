export type Role = "USER" | "ADMIN";

export type UserSummary = {
  id: string;
  username: string;
  displayName: string;
  role: Role;
};

export type PhotoSummary = {
  id: string;
  ownerId: string;
  parentType: "POST" | "ALBUM";
  parentId: string;
  caption?: string | null;
  originalFilename: string;
  contentType: string;
  fileSize: number;
  createdAt: string;
  updatedAt: string;
};

export type CommentSummary = {
  id: string;
  ownerId: string;
  targetType: "POST" | "PHOTO";
  targetId: string;
  body: string;
  createdAt: string;
  updatedAt: string;
  edited: boolean;
};

export type PostSummary = {
  id: string;
  ownerId: string;
  body: string;
  createdAt: string;
  updatedAt: string;
  photos: PhotoSummary[];
  comments: CommentSummary[];
};

export type AlbumSummary = {
  id: string;
  ownerId: string;
  title: string;
  description?: string | null;
  createdAt: string;
  updatedAt: string;
  photos: PhotoSummary[];
};

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    credentials: "include",
    headers: init?.body instanceof FormData ? undefined : { "Content-Type": "application/json" },
    ...init,
  });
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || response.statusText);
  }
  if (response.status === 204 || response.headers.get("content-length") === "0") {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

export const api = {
  me: () => request<UserSummary>("/api/auth/me"),
  login: (username: string, password: string) =>
    request<UserSummary>("/api/auth/login", { method: "POST", body: JSON.stringify({ username, password }) }),
  logout: () => request<void>("/api/auth/logout", { method: "POST" }),
  users: () => request<UserSummary[]>("/api/admin/users"),
  createUser: (input: { username: string; displayName: string; password: string; role: Role }) =>
    request<UserSummary>("/api/admin/users", { method: "POST", body: JSON.stringify(input) }),
  posts: () => request<PostSummary[]>("/api/posts"),
  createPost: (body: string, files: FileList | null) => {
    const form = new FormData();
    form.append("body", body);
    Array.from(files ?? []).forEach((file) => form.append("files", file));
    return request<PostSummary>("/api/posts", { method: "POST", body: form });
  },
  addComment: (targetType: "POST" | "PHOTO", targetId: string, body: string) =>
    request<CommentSummary>(`/api/comments/${targetType}/${targetId}`, { method: "POST", body: JSON.stringify({ body }) }),
  albums: () => request<AlbumSummary[]>("/api/albums"),
  album: (id: string) => request<AlbumSummary>(`/api/albums/${id}`),
  createAlbum: (title: string, description: string) =>
    request<AlbumSummary>("/api/albums", { method: "POST", body: JSON.stringify({ title, description }) }),
  uploadAlbumPhotos: (albumId: string, files: FileList | null) => {
    const form = new FormData();
    Array.from(files ?? []).forEach((file) => form.append("files", file));
    return request<PhotoSummary[]>(`/api/albums/${albumId}/photos`, { method: "POST", body: form });
  },
  photo: (id: string) => request<PhotoSummary>(`/api/photos/${id}`),
  comments: (targetType: "POST" | "PHOTO", targetId: string) =>
    request<CommentSummary[]>(`/api/comments/${targetType}/${targetId}`),
};
