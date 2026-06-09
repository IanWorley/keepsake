import { StrictMode, useEffect, useMemo, useState } from "react";
import { createRoot } from "react-dom/client";
import { Camera, Images, LogOut, MessageCircle, Plus, Send, Shield, Upload, UserPlus } from "lucide-react";
import "./index.css";
import { api, AlbumSummary, CommentSummary, PhotoSummary, PostSummary, UserSummary } from "./api";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Textarea } from "@/components/ui/textarea";

function App() {
  const [user, setUser] = useState<UserSummary | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.me().then(setUser).catch(() => setUser(null)).finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="flex min-h-screen items-center justify-center text-sm text-stone-600">Loading Keepsake</div>;
  }

  if (!user) {
    return <Login onLogin={setUser} />;
  }

  return <Shell user={user} onLogout={() => setUser(null)} />;
}

function Login({ onLogin }: { onLogin: (user: UserSummary) => void }) {
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin");
  const [error, setError] = useState("");

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setError("");
    try {
      onLogin(await api.login(username, password));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Could not log in");
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center px-4 py-10">
      <Card className="w-full max-w-sm">
        <CardHeader>
          <div className="mb-4 flex h-11 w-11 items-center justify-center rounded-md bg-stone-950 text-white">
            <Camera className="h-5 w-5" />
          </div>
          <CardTitle className="text-xl">Keepsake</CardTitle>
          <p className="text-sm text-stone-600">Private posts, albums, photos, and comments.</p>
        </CardHeader>
        <CardContent>
          <form className="space-y-4" onSubmit={submit}>
            <Input value={username} onChange={(event) => setUsername(event.target.value)} placeholder="Username" />
            <Input value={password} onChange={(event) => setPassword(event.target.value)} placeholder="Password" type="password" />
            {error ? <p className="text-sm text-red-600">{error}</p> : null}
            <Button className="w-full" type="submit">Sign in</Button>
          </form>
        </CardContent>
      </Card>
    </main>
  );
}

function Shell({ user, onLogout }: { user: UserSummary; onLogout: () => void }) {
  async function logout() {
    await api.logout();
    onLogout();
  }

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-10 border-b border-stone-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
          <div className="flex items-center gap-3">
            <div className="flex h-9 w-9 items-center justify-center rounded-md bg-stone-950 text-white">
              <Camera className="h-5 w-5" />
            </div>
            <div>
              <h1 className="text-lg font-semibold leading-tight">Keepsake</h1>
              <p className="text-xs text-stone-500">{user.displayName}</p>
            </div>
          </div>
          <Button variant="ghost" size="sm" onClick={logout}>
            <LogOut className="h-4 w-4" /> Logout
          </Button>
        </div>
      </header>

      <main className="mx-auto max-w-6xl px-4 py-6">
        <Tabs defaultValue="feed" className="space-y-5">
          <TabsList>
            <TabsTrigger value="feed">Feed</TabsTrigger>
            <TabsTrigger value="albums">Albums</TabsTrigger>
            <TabsTrigger value="admin" disabled={user.role !== "ADMIN"}>Admin</TabsTrigger>
          </TabsList>
          <TabsContent value="feed">
            <Feed user={user} />
          </TabsContent>
          <TabsContent value="albums">
            <Albums />
          </TabsContent>
          <TabsContent value="admin">
            <AdminUsers />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
}

function Feed({ user }: { user: UserSummary }) {
  const [posts, setPosts] = useState<PostSummary[]>([]);
  const [body, setBody] = useState("");
  const [files, setFiles] = useState<FileList | null>(null);

  async function load() {
    setPosts(await api.posts());
  }

  useEffect(() => {
    void load();
  }, []);

  async function create(event: React.FormEvent) {
    event.preventDefault();
    if (!body.trim() && (!files || files.length === 0)) return;
    await api.createPost(body, files);
    setBody("");
    setFiles(null);
    await load();
  }

  return (
    <div className="mx-auto max-w-[560px] space-y-4">
      <Card className="shadow-sm">
        <CardContent className="pt-4">
          <form className="space-y-3" onSubmit={create}>
            <div className="flex items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-stone-200 text-stone-600 text-sm font-medium">
                {user.displayName[0]?.toUpperCase()}
              </div>
              <div className="flex-1">
                <Textarea
                  value={body}
                  onChange={(event) => setBody(event.target.value)}
                  placeholder={`What's on your mind, ${user.displayName.split(" ")[0]}?`}
                  className="min-h-[60px] resize-y border-0 bg-stone-100 px-4 py-3 text-[15px] placeholder:text-stone-500 focus-visible:ring-0"
                />
              </div>
            </div>
            <div className="flex items-center justify-between border-t pt-3">
              <label className="flex cursor-pointer items-center gap-2 rounded-full px-3 py-1.5 text-sm text-stone-600 hover:bg-stone-100">
                <Images className="h-5 w-5" /> Photo
                <Input type="file" accept="image/jpeg,image/png,image/webp" multiple className="hidden" onChange={(event) => setFiles(event.target.files)} />
              </label>
              <Button type="submit" className="rounded-full px-6"><Send className="h-4 w-4" /> Post</Button>
            </div>
          </form>
        </CardContent>
      </Card>
      <div className="space-y-3">
        {posts.map((post) => <PostCard key={post.id} post={post} user={user} onChange={load} />)}
      </div>
    </div>
  );
}

function PostCard({ post, user, onChange }: { post: PostSummary; user: UserSummary; onChange: () => Promise<void> }) {
  return (
    <Card className="overflow-hidden shadow-sm">
      <div className="flex items-center gap-3 px-4 pt-4">
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-stone-200 text-stone-600 text-sm font-medium">U</div>
        <div className="flex-1">
          <div className="font-semibold text-[15px]">User</div>
          <div className="text-xs text-stone-500">{new Date(post.createdAt).toLocaleString()}</div>
        </div>
      </div>
      <CardContent className="space-y-3 pt-3">
        {post.body ? <p className="preserve-lines px-1 text-[15px] leading-6">{post.body}</p> : null}
        <PhotoGrid photos={post.photos} />
        <div className="border-t pt-2">
          <Comments targetType="POST" targetId={post.id} initial={post.comments} user={user} onChange={onChange} />
        </div>
      </CardContent>
    </Card>
  );
}

function PhotoGrid({ photos }: { photos: PhotoSummary[] }) {
  if (!photos.length) return null;
  return (
    <div className="grid grid-cols-2 gap-2 md:grid-cols-3">
      {photos.map((photo) => (
        <PhotoTile key={photo.id} photo={photo} />
      ))}
    </div>
  );
}

function PhotoTile({ photo }: { photo: PhotoSummary }) {
  const [open, setOpen] = useState(false);
  return (
    <>
      <button className="aspect-square overflow-hidden rounded-md bg-stone-100" onClick={() => setOpen(true)}>
        <img className="h-full w-full object-cover" src={`/api/photos/${photo.id}/file`} alt={photo.caption ?? photo.originalFilename} />
      </button>
      {open ? <PhotoModal photo={photo} onClose={() => setOpen(false)} /> : null}
    </>
  );
}

function PhotoModal({ photo, onClose }: { photo: PhotoSummary; onClose: () => void }) {
  const [comments, setComments] = useState<CommentSummary[]>([]);
  const fakeUser = useMemo<UserSummary>(() => ({ id: "", username: "", displayName: "", role: "USER" }), []);

  useEffect(() => {
    api.comments("PHOTO", photo.id).then(setComments).catch(() => setComments([]));
  }, [photo.id]);

  async function reload() {
    setComments(await api.comments("PHOTO", photo.id));
  }

  return (
    <div className="fixed inset-0 z-20 flex items-center justify-center bg-black/60 p-4" onClick={onClose}>
      <div className="max-h-[92vh] w-full max-w-4xl overflow-auto rounded-lg bg-white p-4" onClick={(event) => event.stopPropagation()}>
        <div className="mb-3 flex items-center justify-between">
          <p className="text-sm font-medium">{photo.caption ?? photo.originalFilename}</p>
          <Button variant="ghost" size="sm" onClick={onClose}>Close</Button>
        </div>
        <img className="max-h-[62vh] w-full rounded-md object-contain bg-stone-100" src={`/api/photos/${photo.id}/file`} alt={photo.caption ?? photo.originalFilename} />
        <div className="mt-4">
          <Comments targetType="PHOTO" targetId={photo.id} initial={comments} user={fakeUser} onChange={reload} />
        </div>
      </div>
    </div>
  );
}

function Comments({ targetType, targetId, initial, onChange }: {
  targetType: "POST" | "PHOTO";
  targetId: string;
  initial: CommentSummary[];
  user: UserSummary;
  onChange: () => Promise<void>;
}) {
  const [body, setBody] = useState("");

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    if (!body.trim()) return;
    await api.addComment(targetType, targetId, body);
    setBody("");
    await onChange();
  }

  return (
    <section className="space-y-2 pt-1">
      <div className="space-y-2">
        {initial.map((comment) => (
          <div key={comment.id} className="flex gap-2">
            <div className="mt-0.5 flex h-7 w-7 items-center justify-center rounded-full bg-stone-200 text-[10px] text-stone-600">U</div>
            <div className="flex-1 rounded-2xl bg-stone-100 px-3 py-2 text-sm">
              <p className="preserve-lines">{comment.body}</p>
              <p className="mt-0.5 text-[10px] text-stone-500">
                {new Date(comment.createdAt).toLocaleString()}{comment.edited ? " · edited" : ""}
              </p>
            </div>
          </div>
        ))}
      </div>
      <form className="flex gap-2 pl-9" onSubmit={submit}>
        <Input value={body} onChange={(event) => setBody(event.target.value)} placeholder="Write a comment..." className="h-9 flex-1 rounded-full bg-stone-100 border-0 text-sm" />
        <Button size="icon" className="h-9 w-9 rounded-full" type="submit" aria-label="Send comment"><Send className="h-4 w-4" /></Button>
      </form>
    </section>
  );
}

function Albums() {
  const [albums, setAlbums] = useState<AlbumSummary[]>([]);
  const [selected, setSelected] = useState<AlbumSummary | null>(null);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [files, setFiles] = useState<FileList | null>(null);

  async function load() {
    const data = await api.albums();
    setAlbums(data);
    if (selected) {
      setSelected(data.find((album) => album.id === selected.id) ?? null);
    }
  }

  useEffect(() => {
    void load();
  }, []);

  async function create(event: React.FormEvent) {
    event.preventDefault();
    const album = await api.createAlbum(title, description);
    setTitle("");
    setDescription("");
    await load();
    setSelected(album);
  }

  async function upload(event: React.FormEvent) {
    event.preventDefault();
    if (!selected) return;
    await api.uploadAlbumPhotos(selected.id, files);
    setFiles(null);
    const album = await api.album(selected.id);
    setSelected(album);
    await load();
  }

  return (
    <div className="grid gap-5 lg:grid-cols-[320px_1fr]">
      <div className="space-y-4">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><Images className="h-4 w-4" /> New album</CardTitle>
          </CardHeader>
          <CardContent>
            <form className="space-y-3" onSubmit={create}>
              <Input value={title} onChange={(event) => setTitle(event.target.value)} placeholder="Title" />
              <Textarea value={description} onChange={(event) => setDescription(event.target.value)} placeholder="Description" />
              <Button type="submit">Create</Button>
            </form>
          </CardContent>
        </Card>
        <div className="space-y-2">
          {albums.map((album) => (
            <button key={album.id} className="w-full rounded-md border border-stone-200 bg-white px-3 py-3 text-left text-sm hover:bg-stone-50" onClick={() => setSelected(album)}>
              <span className="block font-medium">{album.title}</span>
              <span className="text-xs text-stone-500">{album.photos.length} photos</span>
            </button>
          ))}
        </div>
      </div>
      {selected ? (
        <Card>
          <CardHeader>
            <CardTitle>{selected.title}</CardTitle>
            {selected.description ? <p className="text-sm text-stone-600">{selected.description}</p> : null}
          </CardHeader>
          <CardContent className="space-y-4">
            <form className="flex flex-col gap-2 sm:flex-row" onSubmit={upload}>
              <Input type="file" accept="image/jpeg,image/png,image/webp" multiple onChange={(event) => setFiles(event.target.files)} />
              <Button type="submit"><Upload className="h-4 w-4" /> Upload</Button>
            </form>
            <PhotoGrid photos={selected.photos} />
          </CardContent>
        </Card>
      ) : (
        <div className="flex min-h-72 items-center justify-center rounded-lg border border-dashed border-stone-300 bg-white/70 text-sm text-stone-500">
          Select an album
        </div>
      )}
    </div>
  );
}

function AdminUsers() {
  const [users, setUsers] = useState<UserSummary[]>([]);
  const [username, setUsername] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState<"USER" | "ADMIN">("USER");

  async function load() {
    setUsers(await api.users());
  }

  useEffect(() => {
    void load();
  }, []);

  async function create(event: React.FormEvent) {
    event.preventDefault();
    await api.createUser({ username, displayName, password, role });
    setUsername("");
    setDisplayName("");
    setPassword("");
    setRole("USER");
    await load();
  }

  return (
    <div className="grid gap-5 lg:grid-cols-[360px_1fr]">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2"><UserPlus className="h-4 w-4" /> Create user</CardTitle>
        </CardHeader>
        <CardContent>
          <form className="space-y-3" onSubmit={create}>
            <Input value={username} onChange={(event) => setUsername(event.target.value)} placeholder="Username" />
            <Input value={displayName} onChange={(event) => setDisplayName(event.target.value)} placeholder="Display name" />
            <Input value={password} onChange={(event) => setPassword(event.target.value)} placeholder="Password" type="password" />
            <select className="h-10 w-full rounded-md border border-stone-200 bg-white px-3 text-sm" value={role} onChange={(event) => setRole(event.target.value as "USER" | "ADMIN")}>
              <option value="USER">User</option>
              <option value="ADMIN">Admin</option>
            </select>
            <Button type="submit"><Shield className="h-4 w-4" /> Add user</Button>
          </form>
        </CardContent>
      </Card>
      <Card>
        <CardHeader>
          <CardTitle>Users</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="divide-y divide-stone-100">
            {users.map((item) => (
              <div key={item.id} className="flex items-center justify-between py-3 text-sm">
                <div>
                  <p className="font-medium">{item.displayName}</p>
                  <p className="text-stone-500">{item.username}</p>
                </div>
                <span className="rounded-md bg-stone-100 px-2 py-1 text-xs">{item.role}</span>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
