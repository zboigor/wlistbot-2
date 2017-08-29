CREATE TABLE public.ban_votes
(
  id SERIAL PRIMARY KEY NOT NULL,
  chat_id BIGINT NOT NULL,
  vote_message_id INT,
  user_id INT,
  ban BOOLEAN
);
CREATE UNIQUE INDEX ban_votes_id_uindex ON public.ban_votes (id);

CREATE TABLE public.spam_trigger
(
  id SERIAL PRIMARY KEY NOT NULL,
  chat_id BIGINT,
  trigger_text VARCHAR(200) NOT NULL,
  is_global BOOLEAN DEFAULT false
);
CREATE UNIQUE INDEX spam_trigger_id_uindex ON public.spam_trigger (id);

CREATE TABLE public.activity_audit
(
  id SERIAL PRIMARY KEY NOT NULL,
  chat_id BIGINT,
  user_id INT,
  count BIGINT,
  date DATE
);